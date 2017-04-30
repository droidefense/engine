package droidefense.xmodel.manifest;

import droidefense.xmodel.manifest.base.AbstractManifestClass;

import java.util.ArrayList;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class IntentFilter extends AbstractManifestClass {
    //reference object
    //activity, activity alias, service, receiver

    //object vars
    private String icon;
    private String label;
    private int priority;

    //must contain
    private ArrayList<Action> action;

    //can contain
    private ArrayList<Category> categories;
    private ArrayList<Data> data;

    public IntentFilter() {
        categories = new ArrayList<>();
        data = new ArrayList<>();
        action = new ArrayList<>();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public ArrayList<Action> getAction() {
        return action;
    }

    public void setAction(ArrayList<Action> action) {
        this.action = action;
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Activity) {
            ((Activity) this.parent).add(this);
        } else if (parent instanceof ActivityAlias) {
            ((ActivityAlias) this.parent).add(this);
        } else if (parent instanceof Service) {
            ((Service) this.parent).add(this);
        } else {
            ((Receiver) this.parent).add(this);
        }

        //escalate and add in manifest too for fast access
        AbstractManifestClass currentOwner, previous;
        currentOwner = parent;
        do {
            previous = currentOwner.getParent();
            currentOwner = previous;
        } while (!(currentOwner instanceof Manifest));
        ((Manifest) currentOwner).addFilter(this);
    }

    public void add(Action action) {
        this.action.add(action);
    }

    public void add(Category c) {
        this.categories.add(c);
    }

    public void add(Data d) {
        this.data.add(d);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
