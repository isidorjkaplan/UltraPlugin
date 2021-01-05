package co.amscraft.ultralib.editor;

/**
 * Created by Izzy on 2017-10-01.
 */
@Deprecated
public class EditorOld {
/*
    public List<Object> editing = new ArrayList<>();
    private boolean resend = true;
    private Map<String, Object> map = new HashMap<>();
    public static <P> P parse(Class<P> type, String string) {
        try {
            return parse(type, (Object) string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <P> P parse(Class<P> type, Object value) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //System.out.println( "Class=" + type.getName() + ", Value="+value);
        if (value.toString().equals("null")) {
            return null;
        }
        if (type.getClass().equals(value.getClass()) || type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }
        if (type.isPrimitive()) {
            Class<?> nonPrimitive = Class.forName(("java.lang." + (Character.toString(Character.toUpperCase(type.getSimpleName().charAt(0))) + type.getSimpleName().substring(1))).replace("Int", "Integer"));
            return (P) nonPrimitive.getMethod(type.getSimpleName() + "Value").invoke(nonPrimitive.getMethod("parse" + nonPrimitive.getSimpleName().replace("Integer", "Int"), String.class).invoke(null, value));
        }
        if (type.isEnum()) {
            //System.out.println("IS ENUM");
            for (P p : (P[]) type.getMethod("values").invoke(null)) {
                if (p.toString().equalsIgnoreCase(value.toString())) {
                    return p;
                }
            }
        }
        Exception exception = null;
        List<Method> methods = EditorData.getParse(type);
        for (Class<?> child : type.getClasses()) {
            methods.addAll(EditorData.getParse(child));
        }
        for (Method m : methods) {
            //System.out.println(m);
            try {
                Class<?> param = m.getParameterTypes()[0];
                value = parse(param, value);
                Object object = m.invoke(null, value);
                return (P) object;
            } catch (Exception e) {
                exception = e;
            }
        }
        if (exception != null) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Field getField(Class<?> type, String field) {
        for (Field f : ObjectUtils.getFields(type)) {
            if (!isStatic(f) && f.getName().equals(field) || (f.getAnnotationsByType(FieldDescription.class).length > 0 && f.getAnnotationsByType(FieldDescription.class)[0].display().equals(field))) {
                return f;
            }
        }
        return null;
    }

    public static String getFieldName(Field field) {
        if (field.getAnnotationsByType(FieldDescription.class).length > 0 && !field.getAnnotation(FieldDescription.class).display().equals("")) {
            return field.getAnnotation(FieldDescription.class).display();
        }
        return field.getName();
    }

    public static Class<?> getListType(Field stringListField) {
        ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
    }

    public static Class<?> getType(Field field) {
        if (field.getType().equals(Object.class)) {
            try {
                //System.out.println(field.getGenericType().getTypeName());
                return Object.class;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return field.getType();
    }

    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    public static boolean isFieldEnabled(Field field) {
        return field != null && !isStatic(field) && field.getAnnotation(FieldDescription.class) == null || field.getAnnotation(FieldDescription.class).show();
    }

    public Class<?> getListType() {
        if (this.getObject() instanceof List) {
            Object previous = this.editing.get(this.editing.lastIndexOf(this.getObject()) - 1);
            if (previous instanceof Class<?>) {
                return (Class<?>) previous;
            } else {
                return getListType(getParentField());
            }
        }
        return null;
    }

    public Field getParentField() {
        Object previous = this.editing.get(this.editing.lastIndexOf(this.getObject()) - 1);
        for (Field field : ObjectUtils.getFields(previous.getClass())) {
            try {
                if (field.get(previous) == (this.getObject())) {
                    return field;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Object getObject() {
        return this.editing.get(editing.size() - 1);
    }

    private void sendMessage(String message) {
        sendMessage(message, true);
    }

    private void sendMessage(String message, boolean cancel) {
        resend = !cancel;
        this.getPlayer().getBukkit().sendMessage(this.getPlayer().getData(EditorSettings.class).getHelp() + message);
    }

    public boolean isList() {
        return List.class.isAssignableFrom(this.getObject().getClass());
    }

    public void run(String key, String value) {
        this.resend = true;
        EditorRunResponseEvent event = new EditorRunResponseEvent(this.getPlayer(), key, value);
        Bukkit.getServer().getPluginManager().callEvent(event);
        key = event.getKey();
        value = event.getValue();
        if (!event.isCanceled()) {
            if (value != null && value.equals("")) {
                value = null;
            }
            switch (key.toLowerCase()) {
                case "back":
                    back();
                    break;
                case "help":
                    sendHelp(this.getPlayer().getBukkit());
                    break;
                case "delete":
                    try {
                        this.getObject().getClass().getMethod("delete").invoke(this.getObject());
                        this.editing.remove(this.editing.size() - 1);
                    } catch (Exception e) {
                        sendMessage("You cannot delete this object!");
                    }
                    break;
                default:
                    if (isList()) {
                        List list = (List) this.getObject();
                        switch (key.toLowerCase()) {
                            case "new":
                                try {
                                    if (EditorData.getConstructors(getListType()).isEmpty()) {
                                        Object object = getListType().newInstance();
                                        if (!list.contains(object)) {
                                            list.add(object);
                                        }
                                        this.getEditing().add(object);
                                    } else {
                                        this.getEditing().add(getListType());
                                    }
                                } catch (Exception e) {
                                    sendMessage("You must enter a valid variable to edit!");
                                }
                                break;
                            case "add": {
                                Object object = null;
                                if (!getListType().isEnum()) {
                                    object = parse(getListType(), value);
                                } else {
                                    object = Enum.valueOf((Class<? extends Enum>) getListType(), value);
                                }
                                if (object != null) {
                                    if (!list.contains(object)) {
                                        Field field = getParentField();
                                        if (EditorCheck.check(field, object, this.getPlayer().getBukkit())) {
                                            list.add(object);
                                        } else {
                                            sendMessage(EditorCheck.getCheck(field).getFailMessage());
                                        }
                                    } else {
                                        sendMessage("This item is already in the list");
                                    }
                                } else {
                                    sendMessage("The item you have entered does not exist!");
                                }
                            }
                            break;
                            case "remove":
                                try {
                                    Object object = null;
                                    if (!getListType().isEnum()) {
                                        object = parse(getListType(), value);
                                    } else {
                                        object = Enum.valueOf((Class<? extends Enum>) getListType(), value);
                                    }
                                    if (object == null && value != null) {
                                        for (Object o : list) {
                                            if (o.toString().toLowerCase().contains(value.toLowerCase())) {
                                                object = o;
                                                break;
                                            }
                                        }
                                    }
                                    Integer index = null;
                                    if (object == null) {
                                        index = Integer.parseInt(value);
                                    }
                                    if (object != null) {
                                        if (list.contains(object)) {
                                            list.remove(object);
                                        } else {
                                            sendMessage("This item is not on the list!");
                                        }
                                    } else {
                                        if (list.size() > index) {
                                            list.remove((int) index);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "clear":
                                list.clear();
                                break;
                            case "copy":
                                try {
                                    Object object = null;
                                    if (!getListType().isEnum()) {
                                        object = parse(getListType(), value);
                                    } else {
                                        object = Enum.valueOf((Class<? extends Enum>) getListType(), value);
                                    }
                                    if (object == null && value != null) {
                                        for (Object o : list) {
                                            if (o.toString().toLowerCase().contains(value.toLowerCase())) {
                                                object = o;
                                                break;
                                            }
                                        }
                                    }
                                    if (object != null) {
                                        Object object2 = ObjectUtils.clone(object);
                                        //System.out.println(object2);
                                        if (!list.contains(object2)) {
                                            list.add(object2);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    Object object = parse(getListType(), (String) key);
                                    try {
                                        for (Object o : ((List) this.getObject())) {
                                            if (o.toString().equalsIgnoreCase(key)) {
                                                object = o;
                                                break;
                                            }
                                        }
                                        if (object == null && Integer.parseInt(key) < list.size()) {
                                            object = Integer.parseInt(key);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (object != null) {
                                        if (list.contains(object) || (object instanceof Integer && !getListType().equals(int.class) && !getListType().equals(Integer.class) && list.size() > (int) object)) {
                                            if (object instanceof Integer && !getListType().equals(Integer.class)) {
                                                this.getEditing().add(list.get((int) object));
                                            } else if (object.getClass().isPrimitive() || object.getClass().getPackage().toString().contains("java.lang")) {
                                                this.sendMessage("You cannot edit a primitive object! Just enter its value!");
                                            } else {
                                                this.getEditing().add(object);
                                            }
                                        } else {
                                            sendMessage("This list does not contain that object!");
                                        }
                                    } else {
                                        sendMessage("You must enter a valid variable to edit!");
                                    }
                                } catch (Exception e) {
                                    sendMessage("You must enter a valid variable to edit!");
                                }
                                break;
                        }
                    } else if (this.getObject() instanceof Class<?>) {
                        String[] stringArgs = {};
                        if (value != null) {
                            stringArgs = value.split(", ");
                        }
                        for (Constructor c : EditorData.getConstructors((Class<?>) this.getObject())) {
                            String type = c.getAnnotatedReturnType().getType().getTypeName();
                            type = type.split("[.]")[type.split("[.]").length - 1];
                            if (type.equalsIgnoreCase(key)) {
                                try {
                                    Object[] args = new Object[c.getParameterTypes().length];
                                    for (int i = 0; i < c.getParameterTypes().length; i++) {
                                        args[i] = parse(c.getParameterTypes()[i], (String) stringArgs[i]);
                                        System.out.println(args[i]);
                                    }
                                    this.getEditing().add(c.newInstance(args));
                                    this.editing.remove(this.editing.size() - 2);
                                    if (this.editing.get(this.editing.size() - 2) instanceof List) {
                                        ((List) this.editing.get(this.editing.size() - 2)).add(this.getObject());
                                    } else {
                                        ((Field) this.editing.get(this.editing.size() - 2)).set(this.editing.get(this.editing.size() - 3), this.getObject());
                                        this.editing.remove(this.editing.size() - 2);
                                    }
                                } catch (Exception e) {
                                    sendMessage("You have entered invalid arguments!");
                                }
                                break;
                            }
                        }
                    } else {
                        switch (key.toLowerCase()) {
                            default:
                                if (value != null) {
                                    if (value.equalsIgnoreCase("new")) {
                                        try {
                                            Field field = getField(this.getObject().getClass(), key);
                                            if (isFieldEnabled(field)) {
                                                field.setAccessible(true);
                                                List<Constructor> constructors = EditorData.getConstructors(getType(field));
                                                if (constructors.isEmpty()) {
                                                    field.set(this.getObject(), getType(field).newInstance());
                                                } else {
                                                    this.getEditing().add(field);
                                                    this.getEditing().add(getType(field));
                                                }
                                            }
                                        } catch (Exception e) {
                                            sendMessage("You must enter a valid variable to edit!");
                                        }
                                    } else {
                                        boolean found = false;
                                        for (Field field : ObjectUtils.getFields(this.getObject().getClass())) {
                                            if (isFieldEnabled(field)) {
                                                boolean accessible = field.isAccessible();
                                                field.setAccessible(true);
                                                if (getFieldName(field).equalsIgnoreCase(key)) {
                                                    try {
                                                        boolean show = true;
                                                        if (field.getAnnotationsByType(FieldDescription.class).length != 0) {
                                                            show = field.getAnnotationsByType(FieldDescription.class)[0].show();
                                                        }
                                                        if (show) {
                                                            Object output;
                                                            if (field.getType().isEnum()) {
                                                                output = Enum.valueOf((Class<? extends Enum>) field.getType(), value);
                                                            } else {
                                                                output = parse(getType(field), value);
                                                            }
                                                            if (output != null) {
                                                                //System.out.println(output + ", " + output.getClass());
                                                                if (EditorCheck.check(field, output, this.getPlayer().getBukkit())) {
                                                                    field.set(this.getObject(), output);
                                                                } else {
                                                                    sendMessage(EditorCheck.getCheck(field).getFailMessage());
                                                                }
                                                                found = true;
                                                            } else {
                                                                sendMessage("You entered an invalid variable option!");
                                                            }
                                                        }
                                                        break;
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                field.setAccessible(accessible);
                                            }
                                        }
                                        if (!found) {
                                            sendMessage("The variable you tried to edit does not exist!");
                                        }
                                        break;
                                    }
                                } else {
                                    try {
                                        if (!getField(this.getObject().getClass(), key).getType().isEnum()) {
                                            Object object = getField(this.getObject().getClass(), key).get(this.getObject());
                                            if (object != null) {
                                                if (object.getClass().isPrimitive() || object.getClass().getPackage().toString().contains("java.lang")) {
                                                    this.sendMessage("You cannot edit a primitive object! Just enter its value!");
                                                } else {
                                                    this.getEditing().add(object);
                                                }
                                            } else {
                                                this.sendMessage("You must first set the value of the object! Please enter the value 'new' to create a new object!");
                                            }
                                        } else {
                                            EditorSettings settings = getPlayer().getData(EditorSettings.class);
                                            List<String> strings = new ArrayList<>();
                                            for (Object o : (Object[]) ((Class<? extends Enum>) getField(this.getObject().getClass(), key).getType()).getMethod("values").invoke(null)) {
                                                try {
                                                    strings.add(o.getClass().getMethod("getName").invoke(o) + "");
                                                } catch (Throwable e) {
                                                    strings.add(o.toString());
                                                }

                                            }
                                            getPlayer().getBukkit().sendMessage(settings.getVariable() + "Valid Options" + settings.getColon() + ": " + settings.getValue() + strings.toString());
                                            this.resend = false;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                        }
                    }
                    break;
            }
            if (this.resend) {
                sendChat(getPlayer().getBukkit());
            }
        }
    }

    public void back() {
        if (this.getObject() instanceof UltraObject) {
            ((UltraObject) this.getObject()).save();
        }
        this.editing.remove(this.editing.size() - 1);
        if (this.editing.isEmpty() || (this.getObject() instanceof Class<?> && this.editing.size() == 1)) {
            this.sendMessage("You have exited the editor!");
            this.getPlayer().removeData(EditorOld.class);
        }

    }

    public String getTitle() {
        EditorSettings settings = this.getPlayer().getData(EditorSettings.class);
        if (this.getObject() instanceof Class<?>) {
            return settings.getTitle().replace("{CLASS}", "New " + ((Class<?>) this.getObject()).getSimpleName());
        } else if (!isList()) {
            return (settings.getTitle().replace("{CLASS}", this.getObject().getClass().getSimpleName()));
        } else {
            return (settings.getTitle().replace("{CLASS}", this.getObject().getClass().getSimpleName()) + "<" + this.getListType().getSimpleName() + ">");
        }
    }

    @Deprecated
    public void passInventoryClick(InventoryClickEvent evt) {
        ItemStack stack = evt.getCurrentItem();
        EditorSettings settings = this.getPlayer().getData(EditorSettings.class);
        String key = stack.getItemMeta().getDisplayName().replace(settings.getVariable() + "", "").split(":")[0];
        switch (key) {
            case "Back":
                map.remove("command");
                this.back();
                return;
            case "New":
                if (!isList()) {
                    map.put("command", "new");
                } else {
                    this.run("new", "");
                }
                break;
            default:
                switch (map.getOrDefault("command", "none").toString()) {
                    case "add":
                        map.remove("command");
                        break;
                    case "new":
                        map.remove("command");
                        if (!isList()) {
                            EditorOld.this.run(key, "new");
                        }
                        break;
                    case "remove":
                        map.remove("command");
                        if (this.isList()) {
                            EditorOld.this.run("remove", key);
                        }
                        break;
                    default:
                        break;
                }

        }


    }

    @Deprecated
    private void sendInventory(Player sender) {
        EditorSettings settings = this.getPlayer().getData(EditorSettings.class);
        String title = this.getTitle();
        List<ItemStack> items = new ArrayList<>();
        ItemStack[] controlls = new ItemStack[9];
        controlls[8] = ObjectUtils.getItemStack(Material.STAINED_GLASS_PANE, 1, settings.getVariable() + "Back", settings.getHelp() + "Click to go back a menu");
        controlls[7] = ObjectUtils.getItemStack(Material.STAINED_GLASS_PANE, 1, settings.getVariable() + "Mode", settings.getHelp() + "Click to go switch to  the chat editor", (byte) 1);
        if (this.getObject() instanceof Class<?>) {
            for (Constructor c : EditorData.getConstructors((Class<?>) this.getObject())) {
                Material type = Material.STICK;
                if (c.getAnnotation(FieldDescription.class) != null) {
                    type = ((FieldDescription) c.getAnnotation(FieldDescription.class)).getIcon();
                }
                //ItemStack stack = new ItemStack(type);
                String name = settings.getVariable() + c.getName() + settings.getColon() + ": " + settings.getValue();
                for (Parameter p : c.getParameters()) {
                    name += "{" + p.getType() + "}, ";
                }
                items.add(ObjectUtils.getItemStack(type, 1, name, ((Class<?>) this.getObject()).getSimpleName()));
            }
        } else if (!isList()) {
            for (Field field : ObjectUtils.getFields(this.getObject().getClass())) {
                boolean isAccessable = field.isAccessible();
                field.setAccessible(true);
                try {
                    String unit = "";
                    boolean show = true;
                    Material item = Material.STICK;
                    String help = "";
                    FieldDescription[] descriptions = field.getAnnotationsByType(FieldDescription.class);
                    if (descriptions.length != 0) {
                        unit = " " + descriptions[0].unit();
                        show = descriptions[0].show();
                        item = descriptions[0].getIcon();
                        help = descriptions[0].help();
                    }
                    if (show) {
                        Object object = field.get(this.getObject());
                        if (object instanceof ChatColor || object instanceof net.md_5.bungee.api.ChatColor) {
                            object = ((ChatColor) object).name();
                        }
                        String name = (settings.getVariable() + getFieldName(field) + settings.getColon() + ": " + settings.getValue() + object + unit);
                        String lore = (settings.getVariable() + "Type" + settings.getColon() + ": " + settings.getValue() + " " + getType(field).getSimpleName());
                        //sender.sendMessage(settings.getVariable() + getFieldName(field) + settings.getColon() + ": " + settings.getValue() + object + unit);
                        items.add(ObjectUtils.getItemStack(item, 1, name, lore));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                field.setAccessible(isAccessable);
            }
        } else {
            for (Object o : (List) this.getObject()) {
                Material material = Material.STICK;
                Class<?> type = o.getClass();
                if (type.getAnnotationsByType(FieldDescription.class).length > 0) {
                    material = type.getAnnotation(FieldDescription.class).getIcon();
                }
                if (material == Material.STICK && type.getConstructors()[0].getAnnotation(FieldDescription.class) != null) {
                    material = type.getConstructors()[0].getAnnotation(FieldDescription.class).getIcon();
                }

                items.add(ObjectUtils.getItemStack(material, 1, settings.getVariable() + o.toString(), settings.getHelp() + "Click to edit"));
            }
            //TODO
            if (!this.getListType().isAssignableFrom(Enum.class)) {
                controlls[0] = ObjectUtils.getItemStack(Material.STAINED_GLASS_PANE, 1, settings.getVariable() + "New", settings.getHelp() + "Add a new item to the list");
            } else {
                controlls[0] = ObjectUtils.getItemStack(Material.STAINED_GLASS_PANE, 1, settings.getVariable() + "Add", settings.getHelp() + "Add an existing item to the list");
            }
            controlls[1] = ObjectUtils.getItemStack(Material.STAINED_GLASS_PANE, 1, settings.getVariable() + "Remove", settings.getHelp() + "Remove an item from the list");
        }
        Inventory inventory = Bukkit.createInventory(sender, items.size() + 9, title);
        inventory.setContents(controlls);
        for (int i = 9; i < inventory.getSize(); i++) {
            inventory.setItem(i, items.get(i - 9));
        }
    }

    public void sendChat(CommandSender sender) {
        EditorSettings settings = this.getPlayer().getData(EditorSettings.class);
        if (this.getObject() instanceof Class<?>) {
            sender.sendMessage(settings.getTitle().replace("{CLASS}", "New " + ((Class<?>) this.getObject()).getSimpleName()));
            for (Constructor c : EditorData.getConstructors((Class<?>) this.getObject())) {
                String message = "";
                try {
                    message += settings.getVariable() + Class.forName(c.getAnnotatedReturnType().getType().getTypeName()).getSimpleName() + settings.getColon() + ": ";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Parameter p : c.getParameters()) {
                    message += "{" + p.getType() + "}, ";
                }
                sender.sendMessage(message);
            }
        } else if (!isList()) {
            sender.sendMessage(settings.getTitle().replace("{CLASS}", this.getObject().getClass().getSimpleName()));
            for (Field field : ObjectUtils.getFields(this.getObject().getClass())) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    String unit = "";
                    boolean show = true;
                    FieldDescription[] descriptions = field.getAnnotationsByType(FieldDescription.class);
                    if (descriptions.length != 0) {
                        unit = " " + descriptions[0].unit();
                        show = descriptions[0].show();
                    }
                    if (show) {
                        Object object = field.get(this.getObject());
                        if (object instanceof ChatColor || object instanceof net.md_5.bungee.api.ChatColor) {
                            object = ((ChatColor) object).name();
                        }
                        sender.sendMessage(settings.getVariable() + getFieldName(field) + settings.getColon() + ": " + settings.getValue() + object + unit);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                field.setAccessible(accessible);
            }
        } else {
            sender.sendMessage(settings.getTitle().replace("{CLASS}", this.getObject().getClass().getSimpleName()) + "<" + this.getListType().getSimpleName() + ">");
            //if (((List) this.getObject()).size() < 10);
            if (this.getObject() instanceof List && (this.getObject() + "").getBytes().length > 30000) {
                for (Object o : ((List) this.getObject())) {
                    sender.sendMessage(settings.getColon() + " - " + settings.getValue().toString() + o);
                }
            } else {
                sender.sendMessage(settings.getValue().toString() + this.getObject());
            }
        }
        this.sendMessage("Say 'help' to open the help menu!");
    }

    public void sendHelp(CommandSender sender) {
        EditorSettings settings = this.getPlayer().getData(EditorSettings.class);
        if (this.getObject() instanceof Class<?>) {

        } else if (!isList()) {
            sender.sendMessage(settings.getTitle().replace("{CLASS}", this.getObject().getClass().getSimpleName()));
            boolean newMessage = false;
            for (Field field : ObjectUtils.getFields(this.getObject().getClass())) {
                if (isFieldEnabled(field)) {
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    try {
                        String help = "";
                        boolean show = true;
                        FieldDescription[] descriptions = field.getAnnotationsByType(FieldDescription.class);
                        if (descriptions.length != 0) {
                            help = descriptions[0].help();
                            show = descriptions[0].show();
                        }
                        if (show) {
                            sender.sendMessage(settings.getVariable() + getFieldName(field) + settings.getColon() + " (" + settings.getValue() + getType(field).getSimpleName() + settings.getColon() + ") " + settings.getHelp() + help);
                            newMessage = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    field.setAccessible(accessible);
                }
            }
            if (newMessage) {
                sender.sendMessage(settings.getVariable() + "{variable}: new " + settings.getColon() + " - " + settings.getHelp() + "Set an object to a new value");
            }
            try {
                this.getObject().getClass().getMethod("delete");
                sender.sendMessage(settings.getVariable() + "delete" + settings.getColon() + " - " + settings.getHelp() + "Deletes the object from the server forever");
            } catch (Exception e) {

            }
        } else {
            sender.sendMessage(settings.getTitle().replace("{CLASS}", this.getObject().getClass().getSimpleName()) + "<" + getListType().getSimpleName() + ">");
            sender.sendMessage(settings.getVariable() + "new");
            sender.sendMessage(settings.getVariable() + "clear");
            sender.sendMessage(settings.getVariable() + "copy");
            sender.sendMessage(settings.getVariable() + "add" + settings.getColon() + ": " + settings.getHelp() + "{" + getListType().getSimpleName() + "}");
            sender.sendMessage(settings.getVariable() + "remove" + settings.getColon() + ": " + settings.getHelp() + "{" + getListType().getSimpleName() + "}");
            if (getListType().isEnum()) {
                try {
                    List<String> strings = new ArrayList<>();
                    for (Object o : (Object[]) ((Class<? extends Enum>) getListType()).getMethod("values").invoke(null)) {
                        try {
                            strings.add(getListType().getMethod("getName()").invoke(o) + "");
                        } catch (Exception e) {
                            strings.add(o.toString());
                        }

                    }
                    sender.sendMessage(settings.getVariable() + "Valid Options" + settings.getColon() + ": " + settings.getValue() + strings.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        sender.sendMessage(settings.getVariable() + "back");
        this.resend = false;
    }


    @Override
    public void save(FileConfiguration configuration) {
        configuration.set(this.getDataName(), null);
    }*/


}