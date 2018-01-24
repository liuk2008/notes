# GSON解析用法

1、将List集合转换为符合json串

    1、将List集合装换为json字符串
    List<Student> students = new ArrayList<Student>();
    Gson gson = new Gson();
    String json = gson.toJson(students);
   
    2、将json字符串转换为List集合
    Type type = new TypeToken<List<Student>>(){}.getType();
    List<Student> list = gson.fromJson(json, type);

2、将Map集合转化为json串

    1、将Map集合装换为json字符串
    Map<String, String> colors = new HashMap<String, String>();
    Gson gson = new Gson();
    String json = gson.toJson(colors);
   
    2、将json字符串转换为Map集合
    Type type = new TypeToken<Map<String, String>>(){}.getType();
    Map<String, String> map = gson.fromJson(json, type);

3、将对象转换为json串

    1、将对象装换为json字符串
    Student student = new Student("Duke", "Menlo Park");
    Gson gson = new Gson();
    String json = gson.toJson(student);
   
    2、将json字符串转换为单个对象
    String json = "{\"name\":\"Duke\",\"address\":\"Menlo Park\",\"dateOfBirth\":\"Feb 1, 2000 12:00:00 AM\"}";
    Gson gson = new Gson();
    // String[] weekDays = gson.fromJson(daysJson, String[].class); //将json串转换为对应的数组
    Student student = gson.fromJson(json, Student.class);

4、集合与数组之间的转换

    // 当list中的数据类型都一致时可以将list转化为数组
    Object[] array = list.toArray();
    // 在转化为其它类型的数组时需要强制类型转换，并且要使用带参数的toArray方法，参数为对象数组，
    // 将list中的内容放入参数数组中，当参数数组的长度小于list的元素个数时，会自动扩充数组的长度以适应list的长度
    String[] array1 = (String[]) list.toArray(new String[0]); // 自动扩充数组的长度
    // 分配一个长度与list的长度相等的字符串数组
    String[] array2 = (String[]) list.toArray(new String[list.size()]);
   
    // 将数组装换为list，直接使用Arrays的asList方法
    ArrayList<String> list = Arrays.asList(array);

    源码分析：

    l、当list或Set中元素类型单一时，可以使用带参数的toArray方法，参数为目标数组对象，如果目标数组长度小于List或Set的元素个数时，
       在转化时自动把目标数组长度调整到L，如果目标数组长度大于L，转化时将List或Set的元素放到目标数组的前L个位置。
       转化后需要进行强制类型转换，才能得到目标数组。

    2、数组转化为List或Set时需要借助Arrays的asList方法，它将数组转化成一个List，可以用这个List构造Set。
       Set set = new HashSet(Arrays.asList(array));

