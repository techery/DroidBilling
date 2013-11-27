// Code generated by dagger-compiler.  Do not edit.
package com.techery.droid.billings.tasks;


import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;

/**
 * A {@code Binder<BillingTask>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 * 
 * Owning the dependency links between {@code BillingTask} and its
 * dependencies.
 * 
 * Being a {@code MembersInjector<BillingTask>} and handling injection
 * of annotated fields.
 */
public final class BillingTask$$InjectAdapter extends Binding<BillingTask>
    implements MembersInjector<BillingTask> {
  private Binding<android.content.Context> context;
  private Binding<de.greenrobot.event.EventBus> bus;
  private Binding<com.techery.droid.billings.utils.ResponseHelper> responseHelper;
  private Binding<com.android.vending.billing.IInAppBillingService> service;

  public BillingTask$$InjectAdapter() {
    super(null, "members/com.techery.droid.billings.tasks.BillingTask", NOT_SINGLETON, BillingTask.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    context = (Binding<android.content.Context>) linker.requestBinding("android.content.Context", BillingTask.class, getClass().getClassLoader());
    bus = (Binding<de.greenrobot.event.EventBus>) linker.requestBinding("de.greenrobot.event.EventBus", BillingTask.class, getClass().getClassLoader());
    responseHelper = (Binding<com.techery.droid.billings.utils.ResponseHelper>) linker.requestBinding("com.techery.droid.billings.utils.ResponseHelper", BillingTask.class, getClass().getClassLoader());
    service = (Binding<com.android.vending.billing.IInAppBillingService>) linker.requestBinding("com.android.vending.billing.IInAppBillingService", BillingTask.class, getClass().getClassLoader());
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    injectMembersBindings.add(context);
    injectMembersBindings.add(bus);
    injectMembersBindings.add(responseHelper);
    injectMembersBindings.add(service);
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<BillingTask>}.
   */
  @Override
  public void injectMembers(BillingTask object) {
    object.context = context.get();
    object.bus = bus.get();
    object.responseHelper = responseHelper.get();
    object.service = service.get();
  }
}
