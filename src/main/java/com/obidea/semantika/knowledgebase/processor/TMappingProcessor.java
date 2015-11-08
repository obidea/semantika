/*
 * Copyright (c) 2013-2015 Obidea Technology
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.obidea.semantika.knowledgebase.processor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.slf4j.Logger;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import com.obidea.semantika.knowledgebase.model.IKnowledgeBase;
import com.obidea.semantika.mapping.MutableMappingSet;
import com.obidea.semantika.mapping.base.ClassMapping;
import com.obidea.semantika.mapping.base.IClassMapping;
import com.obidea.semantika.mapping.base.IMapping;
import com.obidea.semantika.mapping.base.IPropertyMapping;
import com.obidea.semantika.mapping.base.PropertyMapping;
import com.obidea.semantika.mapping.base.TripleAtom;
import com.obidea.semantika.mapping.base.sql.SqlQuery;
import com.obidea.semantika.ontology.owlapi.AbstractOwlOntology;
import com.obidea.semantika.util.LogUtils;
import com.obidea.semantika.util.MultiMap;

public class TMappingProcessor extends OwlObjectHandler implements IKnowledgeBaseProcessor
{
   private AbstractOwlOntology mOntology;

   private IgnoredAxioms mIgnoredAxioms = new IgnoredAxioms();

   private static OWLDataFactory sOwlDataFactory = new OWLDataFactoryImpl();

   /*
    * Some selected TBox axioms types that need to be processed by the TMapping processor. *DO NOT
    * TOUCH* The order is important to ensure a proper mapping generation.
    */
   public static final List<AxiomType<?>> TBoxAxiomTypes = new ArrayList<AxiomType<?>>();
   static {
      TBoxAxiomTypes.add(AxiomType.SUB_OBJECT_PROPERTY);
      TBoxAxiomTypes.add(AxiomType.SUB_DATA_PROPERTY);
      TBoxAxiomTypes.add(AxiomType.SUBCLASS_OF);
      TBoxAxiomTypes.add(AxiomType.OBJECT_PROPERTY_DOMAIN);
      TBoxAxiomTypes.add(AxiomType.OBJECT_PROPERTY_RANGE);
      TBoxAxiomTypes.add(AxiomType.DATA_PROPERTY_DOMAIN);
      TBoxAxiomTypes.add(AxiomType.INVERSE_OBJECT_PROPERTIES);
      TBoxAxiomTypes.add(AxiomType.SYMMETRIC_OBJECT_PROPERTY);
      TBoxAxiomTypes.add(AxiomType.EQUIVALENT_DATA_PROPERTIES);
      TBoxAxiomTypes.add(AxiomType.EQUIVALENT_OBJECT_PROPERTIES);
      TBoxAxiomTypes.add(AxiomType.EQUIVALENT_CLASSES);
   }

   private static final Logger LOG = LogUtils.createLogger("semantika.knowledgebase.processor"); //$NON-NLS-1$

   public TMappingProcessor()
   {
      // NO-OP
   }

   private static MutableMappingSet mutableMappingSet(IKnowledgeBase kb) throws KnowledgeBaseProcessorException
   {
      if (!(kb.getMappingSet() instanceof MutableMappingSet)) {
         throw new KnowledgeBaseProcessorException("Optimization requires mutable mapping set object"); //$NON-NLS-1$
      }
      return (MutableMappingSet) kb.getMappingSet();
   }

   private static AbstractOwlOntology getOwlOntology(IKnowledgeBase kb) throws KnowledgeBaseProcessorException
   {
      if (!(kb.getOntology() instanceof AbstractOwlOntology)) {
         throw new KnowledgeBaseProcessorException("Optimization requires an OWL ontology object"); //$NON-NLS-1$
      }
      return (AbstractOwlOntology) kb.getOntology();
   }

   @Override
   public void optimize(IKnowledgeBase kb) throws KnowledgeBaseProcessorException
   {
      mIgnoredAxioms.clear();
      mMappingSet = mutableMappingSet(kb);
      mOntology = getOwlOntology(kb);
      mOntology.asOwlOntology().accept(this);
      logIgnoreOwlAxiom(); // print all the ignored axioms
   }

   @Override
   public void visit(OWLOntology ontology)
   {
      /*
       * Iterate over the selected TBox axiom types, including the import closure
       */
      for (AxiomType<?> type : TBoxAxiomTypes) {
         for (OWLAxiom axiom : ontology.getAxioms(type, Imports.INCLUDED)) {
            axiom.accept(this);
         }
      }
      /*
       * Iterate other types of axiom (exclude the selected TBox axiom types) for debugging purpose.
       */
      for (OWLAxiom axiom : ontology.getAxioms(Imports.INCLUDED)) {
         AxiomType<?> type = axiom.getAxiomType();
         if (!TBoxAxiomTypes.contains(type)) {
            axiom.accept(this);
         }
      }
   }

   /**
    * A utility method to process OWL <code>SubClassOf(CE1 CE2)</code> axiom and produce inferred
    * mapping assertions.
    */
   @Override
   public void visit(OWLSubClassOfAxiom axiom)
   {
      /*
       * Trace all the ancestors of the class expression in the given OWL SubClassOf axiom.
       */
      Set<OWLSubClassOfAxiom> ancestors = mOntology.traceAncestors(axiom.getSubClass(), true);
      for (OWLSubClassOfAxiom ax : ancestors) {
         /*
          * Get all (copy) known mappings for the visited subclass expression.
          */
         OWLClassExpression subClass = ax.getSubClass();
         subClass.accept(this); // this call will produce (subclass) mSignature
         Set<IMapping> subClassMappings = getMappingsForClassExpression();
         if (subClassMappings.isEmpty()) {
            continue;
         }
         /*
          * Produce the "extra" mappings for the visited super class expression as many as the known
          * mappings in the subclass expression.
          */
         OWLClassExpression superClass = ax.getSuperClass();
         superClass.accept(this); // this call will produce (super class) mSignature and mIsInverse
         URI superClassSignature = mSignature;
         for (IMapping subClassMapping : subClassMappings) {
            IClassMapping cm = createClassMapping(superClassSignature, subClassMapping, mIsInverse);
            addInferredMapping(cm);
         }
      }
   }

   /**
    * A utility method to process OWL <code>SubDataPropertyOf(DPE1 DPE2)</code> axiom and produce
    * inferred mapping assertions.
    */
   @Override
   public void visit(OWLSubDataPropertyOfAxiom axiom)
   {
      /*
       * Trace all the ancestors of the data property expression in the given OWL SubDataPropertyOf axiom.
       */
      Set<OWLSubPropertyAxiom<?>> ancestors = mOntology.traceAncestors(axiom.getSubProperty(), true);
      for (OWLSubPropertyAxiom<?> ax : ancestors) {
         /*
          * Get all (copy) known mappings for the visited sub data property expression.
          */
         OWLDataPropertyExpression subProperty = (OWLDataPropertyExpression) ax.getSubProperty();
         subProperty.accept(this); // this call will produce (sub property) mSignature
         Set<IMapping> subPropertyMappings = getMappingsForPropertyExpression();
         if (subPropertyMappings.isEmpty()) {
            continue;
         }
         /*
          * Produce the "extra" mappings for the visited super data property expression as many as
          * the known mappings in the sub data property expression.
          */
         OWLDataPropertyExpression superProperty = (OWLDataPropertyExpression) ax.getSuperProperty();
         superProperty.accept(this); // this call will produce (super property) mSignature and mIsInverse
         URI superPropertySignature = mSignature;
         for (IMapping subPropertyMapping : subPropertyMappings) {
            IPropertyMapping pm = createPropertyMapping(superPropertySignature, subPropertyMapping, mIsInverse);
            addInferredMapping(pm);
         }
      }
   }

   /**
    * A utility method to process OWL <code>SubObjectPropertyOf(OPE1 OPE2)</code> axiom and produce
    * inferred mapping assertions.
    */
   @Override
   public void visit(OWLSubObjectPropertyOfAxiom axiom)
   {
      /*
       * Trace all the ancestors of the object property expression in the given OWL SubObjectPropertyOf axiom.
       */
      Set<OWLSubPropertyAxiom<?>> ancestors = mOntology.traceAncestors(axiom.getSubProperty(), true);
      for (OWLSubPropertyAxiom<?> ax : ancestors) {
         /*
          * Get all (copy) known mappings for the visited sub object property expression.
          */
         OWLObjectPropertyExpression subProperty = (OWLObjectPropertyExpression) ax.getSubProperty();
         subProperty.accept(this); // this call will produce (sub property) mSignature
         Set<IMapping> subPropertyMappings = getMappingsForPropertyExpression();
         if (subPropertyMappings.isEmpty()) {
            continue;
         }
         /*
          * Produce the "extra" mappings for the visited super object property expression as many as
          * the known mappings in the sub object property expression.
          */
         OWLObjectPropertyExpression superProperty = (OWLObjectPropertyExpression) ax.getSuperProperty();
         superProperty.accept(this); // this call will produce (super property) mSignature and mIsInverse
         URI superPropertySignature = mSignature;
         for (IMapping subPropertyMapping : subPropertyMappings) {
            IPropertyMapping pm = createPropertyMapping(superPropertySignature, subPropertyMapping, mIsInverse);
            addInferredMapping(pm);
         }
      }
   }

   /**
    * A utility method to process OWL <code>DataPropertyDomain(DPE CE)</code> axiom and produce
    * inferred mapping assertions. This axiom is equivalent to
    * <code>SubClassOf(DataSomeValuesFrom(DPE rdfs:Literal) CE)</code>.
    */
   @Override
   public void visit(OWLDataPropertyDomainAxiom axiom)
   {
      axiom.asOWLSubClassOfAxiom().accept(this);
   }

   /**
    * A utility method to process OWL <code>ObjectPropertyDomain(OPE CE)</code> axiom and produce
    * inferred mapping assertions. This axiom is equivalent to
    * <code>SubClassOf(ObjectSomeValuesFrom(OPE owl:Thing) CE)</code>.
    */
   @Override
   public void visit(OWLObjectPropertyDomainAxiom axiom)
   {
      axiom.asOWLSubClassOfAxiom().accept(this);
   }

   /**
    * A utility method to process OWL <code>ObjectPropertyRange(OPE CE)</code> axiom and produce
    * inferred mapping assertions. This axiom is equivalent to
    * <code>SubClassOf(ObjectSomeValuesFrom(ObjectInverseOf(OPE) owl:Thing) CE)</code>.
    */
   @Override
   public void visit(OWLObjectPropertyRangeAxiom axiom)
   {
      asSubClassOfAxiom(axiom).accept(this);
      resetIsInverse(); // reset the isInverse = false.
   }

   private OWLSubClassOfAxiom asSubClassOfAxiom(OWLObjectPropertyRangeAxiom axiom)
   {
      OWLObjectInverseOf inv = sOwlDataFactory.getOWLObjectInverseOf(axiom.getProperty());
      OWLClassExpression sub = sOwlDataFactory.getOWLObjectSomeValuesFrom(inv, sOwlDataFactory.getOWLThing());
      return sOwlDataFactory.getOWLSubClassOfAxiom(sub, axiom.getRange());
   }

   /**
    * A utility method to process OWL <code>InverseObjectProperties(OPE1 OPE2)</code> axiom and
    * produce inferred mapping assertions. The axiom states object property expression
    * <code>OPE1</code> is an inverse of the object property expression <code>OPE2</code>.
    */
   @Override
   public void visit(OWLInverseObjectPropertiesAxiom axiom)
   {
      OWLObjectPropertyExpression ope1 = axiom.getFirstProperty();
      OWLObjectPropertyExpression ope2 = sOwlDataFactory.getOWLObjectInverseOf(axiom.getSecondProperty());
      OWLEquivalentObjectPropertiesAxiom ax = sOwlDataFactory.getOWLEquivalentObjectPropertiesAxiom(ope1, ope2);
      ax.accept(this);
      resetIsInverse(); // reset the isInverse = false.
   }

   /**
    * A utility method to process OWL <code>SymmetricObjectProperty(OPE)</code> axiom and produce
    * inferred mapping assertions.
    */
   @Override
   public void visit(OWLSymmetricObjectPropertyAxiom axiom)
   {
      OWLObjectPropertyExpression ope = axiom.getProperty();
      ope.accept(this); // this call will produce (object property) mSignature
      URI propertySignature = mSignature;
      
      /*
       * Get all (copy) known mappings for the visited object property expression.
       */
      Set<IMapping> propertyMappings = getMappingsForPropertyExpression();
      if (!propertyMappings.isEmpty()) {
         for (IMapping propertyMapping : propertyMappings) {
            /*
             * Create the inverse mapping that reflect the property's symmetry
             */
            IPropertyMapping pm = createPropertyMapping(propertySignature, propertyMapping, true); // inverse = true
            addInferredMapping(pm);
         }
      }
   }

   /**
    * A utility method to process OWL <code>EquivalentClasses(CE1 CE2)</code> axiom and produce
    * inferred mapping assertions.
    */
   @Override
   public void visit(OWLEquivalentClassesAxiom axiom)
   {
      List<OWLClassExpression> classes = axiom.getClassExpressionsAsList();
      OWLClassExpression ce1 = classes.get(0);
      OWLClassExpression ce2 = classes.get(1);
      
      /*
       * Get all (copy) known mappings for the first equivalent class expression
       * and produce the extra mappings.
       */
      ce1.accept(this);
      Set<IMapping> mappings1 = getMappingsForClassExpression();
      if (!mappings1.isEmpty()) {
         ce2.accept(this);
         produceEquivalentClassMappings(mappings1);
      }
      
      /*
       * Get all (copy) known mappings for the second equivalent class expression
       * and produce the extra mappings.
       */
      ce2.accept(this);
      Set<IMapping> mappings2 = getMappingsForClassExpression();
      if (!mappings2.isEmpty()) {
         ce1.accept(this);
         produceEquivalentClassMappings(mappings2);
      }
   }

   private void produceEquivalentClassMappings(Set<IMapping> mappings)
   {
      URI classSignature = mSignature;
      for (IMapping mapping : mappings) {
         IClassMapping cm = createClassMapping(classSignature, mapping, mIsInverse);
         addInferredMapping(cm);
      }
   }

   /**
    * A utility method to process OWL <code>EquivalentObjectProperties(OPE1 OPE2)</code> axiom and produce
    * inferred mapping assertions.
    */
   @Override
   public void visit(OWLEquivalentDataPropertiesAxiom axiom)
   {
      List<OWLDataPropertyExpression> properties = new ArrayList<OWLDataPropertyExpression>(axiom.getProperties());
      OWLDataPropertyExpression dpe1 = properties.get(0);
      OWLDataPropertyExpression dpe2 = properties.get(1);
      
      /*
       * Get all (copy) known mappings for the first equivalent data property expression
       * and produce the extra mappings.
       */
      dpe1.accept(this);
      Set<IMapping> mappings1 = getMappingsForPropertyExpression();
      if (!mappings1.isEmpty()) {
         dpe2.accept(this);
         produceEquivalentPropertyMappings(mappings1);
      }
      
      /*
       * Get all (copy) known mappings for the second equivalent data property expression
       * and produce the extra mappings.
       */
      dpe2.accept(this);
      Set<IMapping> mappings2 = getMappingsForPropertyExpression();
      if (!mappings2.isEmpty()) {
         dpe1.accept(this);
         produceEquivalentPropertyMappings(mappings2);
      }
   }

   /**
    * A utility method to process OWL <code>EquivalentDataProperties(DPE1 DPE2)</code> axiom and produce
    * inferred mapping assertions.
    */
   @Override
   public void visit(OWLEquivalentObjectPropertiesAxiom axiom)
   {
      List<OWLObjectPropertyExpression> properties = new ArrayList<OWLObjectPropertyExpression>(axiom.getProperties());
      OWLObjectPropertyExpression ope1 = properties.get(0);
      OWLObjectPropertyExpression ope2 = properties.get(1);
      
      /*
       * Get all (copy) known mappings for the first equivalent object property expression
       * and produce the extra mappings.
       */
      ope1.accept(this);
      Set<IMapping> mappings1 = getMappingsForPropertyExpression();
      if (!mappings1.isEmpty()) {
         ope2.accept(this);
         produceEquivalentPropertyMappings(mappings1);
      }
      
      /*
       * Get all (copy) known mappings for the second equivalent object property expression
       * and produce the extra mappings.
       */
      ope2.accept(this);
      Set<IMapping> mappings2 = getMappingsForPropertyExpression();
      if (!mappings2.isEmpty()) {
         ope1.accept(this);
         produceEquivalentPropertyMappings(mappings2);
      }
   }

   private void produceEquivalentPropertyMappings(Set<IMapping> mappings)
   {
      URI propertySignature = mSignature;
      for (IMapping mapping : mappings) {
         IPropertyMapping pm = createPropertyMapping(propertySignature, mapping, mIsInverse);
         addInferredMapping(pm);
      }
   }

   /**
    * Obtains all the ignored axioms during the TMapping processing.
    *
    * @return Returns a list of ignored OWL axioms.
    */
   public List<OWLAxiom> getIgnoreList()
   {
      return new ArrayList<OWLAxiom>(mIgnoredAxioms.asList());
   }

   @Override
   public String getName()
   {
      return "TMapping processor"; // $NON-NLS-1$
   }

   /*
    * Ignored axioms
    */

   @Override
   public void visit(OWLDeclarationAxiom axiom)
   {
      ignoreOwlAxiom("Declaration", axiom);
   }

   @Override
   public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom)
   {
      ignoreOwlAxiom("NegavtiveObjectPropertyAssertion", axiom);
   }

   @Override
   public void visit(OWLAsymmetricObjectPropertyAxiom axiom)
   {
      ignoreOwlAxiom("AsymmetricObjectProperty", axiom);
   }

   @Override
   public void visit(OWLReflexiveObjectPropertyAxiom axiom)
   {
      ignoreOwlAxiom("ReflextiveObjectProperty", axiom);
   }

   @Override
   public void visit(OWLDisjointClassesAxiom axiom)
   {
      ignoreOwlAxiom("DisjointClass", axiom);
   }

   @Override
   public void visit(OWLNegativeDataPropertyAssertionAxiom axiom)
   {
      ignoreOwlAxiom("NegavtiveDataPropertyAssertion", axiom);
   }

   @Override
   public void visit(OWLDifferentIndividualsAxiom axiom)
   {
      ignoreOwlAxiom("DifferentIndividualsAxiom", axiom);
   }

   @Override
   public void visit(OWLDisjointDataPropertiesAxiom axiom)
   {
      ignoreOwlAxiom("DisjointDataProperty", axiom);
   }

   @Override
   public void visit(OWLDisjointObjectPropertiesAxiom axiom)
   {
      ignoreOwlAxiom("DisjointObjectProperty", axiom);
   }

   @Override
   public void visit(OWLObjectPropertyAssertionAxiom axiom)
   {
      ignoreOwlAxiom("ObjectPropertyAssertion", axiom);
   }

   @Override
   public void visit(OWLFunctionalObjectPropertyAxiom axiom)
   {
      ignoreOwlAxiom("FunctionalObjectProperty", axiom);
   }

   @Override
   public void visit(OWLDisjointUnionAxiom axiom)
   {
      ignoreOwlAxiom("DisjointUnion", axiom);
   }

   @Override
   public void visit(OWLDataPropertyRangeAxiom axiom)
   {
      ignoreOwlAxiom("DataPropertyRange", axiom);
   }

   @Override
   public void visit(OWLFunctionalDataPropertyAxiom axiom)
   {
      ignoreOwlAxiom("FunctionalDataProperty", axiom);
   }

   @Override
   public void visit(OWLClassAssertionAxiom axiom)
   {
      ignoreOwlAxiom("ClassAssertion", axiom);
   }

   @Override
   public void visit(OWLDataPropertyAssertionAxiom axiom)
   {
      ignoreOwlAxiom("DataPropertyAssertion", axiom);
   }

   @Override
   public void visit(OWLTransitiveObjectPropertyAxiom axiom)
   {
      ignoreOwlAxiom("TransitiveObjectProperty", axiom);
   }

   @Override
   public void visit(OWLIrreflexiveObjectPropertyAxiom axiom)
   {
      ignoreOwlAxiom("IrreflexiveObjectProperty", axiom);
   }

   @Override
   public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom)
   {
      ignoreOwlAxiom("InverseFunctionalObjectProperty", axiom);
   }

   @Override
   public void visit(OWLSameIndividualAxiom axiom)
   {
      ignoreOwlAxiom("SameIndividual", axiom);
   }

   @Override
   public void visit(OWLSubPropertyChainOfAxiom axiom)
   {
      ignoreOwlAxiom("SubPropertyChainOf", axiom);
   }

   @Override
   public void visit(OWLHasKeyAxiom axiom)
   {
      ignoreOwlAxiom("HasKey", axiom);
   }

   @Override
   public void visit(OWLDatatypeDefinitionAxiom axiom)
   {
      ignoreOwlAxiom("DatatypeDefinition", axiom);
   }

   @Override
   public void visit(SWRLRule axiom)
   {
      ignoreOwlAxiom("SWRLRule", axiom);
   }

   @Override
   public void visit(OWLAnnotationAssertionAxiom axiom)
   {
      ignoreOwlAxiom("AnnotationAssertion", axiom);
   }

   @Override
   public void visit(OWLSubAnnotationPropertyOfAxiom axiom)
   {
      ignoreOwlAxiom("SubAnnotationPropertyOf", axiom);
   }

   @Override
   public void visit(OWLAnnotationPropertyDomainAxiom axiom)
   {
      ignoreOwlAxiom("AnnotationPropertyDomain", axiom);
   }

   @Override
   public void visit(OWLAnnotationPropertyRangeAxiom axiom)
   {
      ignoreOwlAxiom("AnnotationPropertyRange", axiom);
   }

   /*
    * Private utility methods
    */

   private static IClassMapping createClassMapping(URI classSignature, IMapping mapping, boolean isInverse)
   {
      final TripleAtom targetAtom = mapping.getTargetAtom();
      final SqlQuery sourceQuery = mapping.getSourceQuery();
      
      ClassMapping cm = new ClassMapping(classSignature, sourceQuery);
      if (!isInverse) {
         cm.setSubjectMapValue(TripleAtom.getSubject(targetAtom));
      }
      else {
         cm.setSubjectMapValue(TripleAtom.getObject(targetAtom));
      }
      return cm;
   }

   private static IPropertyMapping createPropertyMapping(URI propertySignature, IMapping mapping, boolean isInverse)
   {
      final TripleAtom targetAtom = mapping.getTargetAtom();
      final SqlQuery sourceQuery = mapping.getSourceQuery();
      
      PropertyMapping pm = new PropertyMapping(propertySignature, sourceQuery);
      if (!isInverse) {
         pm.setSubjectMapValue(TripleAtom.getSubject(targetAtom));
         pm.setObjectMapValue(TripleAtom.getObject(targetAtom));
      }
      else {
         pm.setSubjectMapValue(TripleAtom.getObject(targetAtom));
         pm.setObjectMapValue(TripleAtom.getSubject(targetAtom));
      }
      return pm;
   }

   private void logIgnoreOwlAxiom()
   {
      if (!mIgnoredAxioms.isEmpty()) {
         MultiMap<String, OWLAxiom> ignoreMap = mIgnoredAxioms.asMap();
         for (String axiomType : ignoreMap.keySet()) {
            LOG.debug("  - Ignoring OWL {} axiom ({} items)", //$NON-NLS-1$
                  axiomType, ignoreMap.get(axiomType).size());
         }
      }
   }

   private void ignoreOwlAxiom(String axiomType, OWLAxiom axiom)
   {
      mIgnoredAxioms.add(axiomType, axiom);
   }

   /**
    * Utility class to store the ignored axioms when processing the ontology and mapping set
    * to produce the T-Mapping.
    */
   private class IgnoredAxioms
   {
      private MultiMap<String, OWLAxiom> mIgnoreMap = new MultiMap<String, OWLAxiom>();

      public void add(String axiomType, OWLAxiom axiom)
      {
         mIgnoreMap.put(axiomType, axiom);
      }

      public MultiMap<String, OWLAxiom> asMap()
      {
         return mIgnoreMap;
      }

      public Set<OWLAxiom> asList()
      {
         return mIgnoreMap.getAllValues();
      }

      public boolean isEmpty()
      {
         return mIgnoreMap.size() == 0;
      }

      public void clear()
      {
         mIgnoreMap.clear();
      }
   }
}
