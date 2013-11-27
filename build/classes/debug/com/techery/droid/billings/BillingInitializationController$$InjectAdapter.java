// Code generated by dagger-compiler.  Do not edit.
package com.techery.droid.billings;


import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;

/**
 * A {@code Binder<BillingInitializationController>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 * 
 * Owning the dependency links between {@code BillingInitializationController} and its
 * dependencies.
 * 
 * Being a {@code Provider<BillingInitializationController>} and handling creation and
 * preparation of object instances.
 * 
 * Being a {@code MembersInjector<BillingInitializationController>} and handling injection
 * of annotated fields.
 */
public final class BillingInitializationController$$InjectAdapter extends Binding<BillingInitializationController>
    implements MembersInjector<BillingInitializationController> {
  private Binding<com.techery.droid.billings.utils.BillingSupportingChecker> billingSupportingChecker;
  private Binding<AbstractController> supertype;

  public BillingInitializationController$$InjectAdapter() {
    super(null, "members/com.techery.droid.billings.BillingInitializationController", NOT_SINGLETON, BillingInitializationController.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    billingSupportingChecker = (Binding<com.techery.droid.billings.utils.BillingSupportingChecker>) linker.requestBinding("com.techery.droid.billings.utils.BillingSupportingChecker", BillingInitializationController.class, getClass().getClassLoader());
    supertype = (Binding<AbstractController>) linker.requestBinding("members/com.techery.droid.billings.AbstractController", BillingInitializationController.class, getClass().getClassLoader(), false, true);
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    injectMembersBindings.add(billingSupportingChecker);
    injectMembersBindings.add(supertype);
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<BillingInitializationController>}.
   */
  @Override
  public void injectMembers(BillingInitializationController object) {
    object.billingSupportingChecker = billingSupportingChecker.get();
    supertype.injectMembers(object);
  }
}
