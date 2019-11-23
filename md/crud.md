#基础概念

> 安全和幂等

```sh
#http请求的安全和幂等，是指多次调用同一个请求对资源状态的影响
'安全' ---> 请求不会影响资源的状态。只读的请求：GET，HEAD，OPTIONS
'幂等' ---> 多次相同的请求，效果一致
```

```sh
/orgz/members      GET     获取成员列表       -> 只是请求，不改变资源状态        #安全，幂等
/orgz/members/120  GET     获取单个成员
/orgz/members      POST    创建成员          -> 多次请求会新增多条相同的数据     #不安全，不幂等
/orgz/members/120  PUT     修改成员          -> 多次请求都是将id为 120 的员工姓名修改成'wang'  #不安全，幂等
/orgz/members      PUT     批量修改
/orgz/members/120  PATCH   修改成员的部分属性
/orgz/members/120  DELETE  删除成员          -> 多次请求目的都是删除id为 5 的员工 #不安全，幂等
#第一次成功删除，第二次及以后虽资源已不存在，但也得返回 200 OK，不能返回 404
```

```sh
#restful特点
用 URL 描述资源
使用http方法描述行为，使用http状态码来表示不同的结果
使用json交互数据
restful只是一种风格，并不是强制的标准
```

> `POST 转化为 PUT DELETE`

```xml
<!--（1）配置 HiddenHttpMethodFilter，SpringBoot 默认已配置-->
<filter>
    <filter-name>HiddenHttpMethodFilter</filter-name>  
    <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>  
</filter>
```

```html
<!--（2）页面创建（POST表单 + 隐藏标签）-->
<form method="post" th:action="@{/emp/}+${emp.id}">
    <input type="hidden" name="_method" value="delete"> <!--隐藏标签 name + value-->
    <a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
</form>
```
# thymeleaf

##后台逻辑

> 列表：超链接跳转

```java
//员工列表页面
@GetMapping("/list")
public String list(Model model) {
    List<Emp> emps = empMapper.selectList(null); //使用 mybatis-plus
    List<EmpVO> empVOs = emps.stream().map(emp -> {
        Long cityId = emp.getCityId();
        City city = cityMapper.selectById(cityId);

        EmpVO empVO = new EmpVO();
        BeanUtils.copyProperties(emp, empVO);
        empVO.setCity(city);
        return empVO;
    }).collect(Collectors.toList());
    log.info("empVOs: {}", JSON.toJSONString(empVOs, true));

    model.addAttribute("emps", empVOs);
    return "/emps/list";
}
```

> 新增：超链接跳转

```java
//跳转新增页面
@GetMapping("/emp")
public String toAdd(Model model) {
    List<City> cities = cityMapper.selectList(null);
    log.info("city: {}", JSON.toJSONString(cities, true));

    model.addAttribute("citys", cities); //初始化新增页面：城市列表
    return "/emps/emp";
}
```

```java
//新增员工，跳转列表页面
@PostMapping("/emp")
public String addOne(/*@RequestBody*/ EmpVO empVO, Model model) { //表单提交不能用 @RequestBody
    Emp emp = new Emp();
    BeanUtils.copyProperties(empVO, emp);
    emp.setCityId(empVO.getCity().getId());
    int insert = empMapper.insert(emp);
    log.info("insert: {}", JSON.toJSONString(emp, true));

    return "redirect:/crud/list"; //重定向，/代表站点根目录
}
```

> 更新：超链接跳转

```java
//跳转修改页面
@GetMapping("/{id}")
public String toUpdate(@PathVariable Long id, Model model) {
    Emp emp = empMapper.selectById(id);
    City city = cityMapper.selectById(emp.getCityId());
    EmpVO empVO = new EmpVO();
    BeanUtils.copyProperties(emp, empVO);
    empVO.setCity(city);
    log.info("empVO: {}", JSON.toJSONString(empVO, true));
    List<City> citys = cityMapper.selectList(null);
    
    model.addAttribute("emp", empVO);   //用于修改回显 
    model.addAttribute("citys", citys); //初始化修改页面：城市列表
    return "/emps/emp";
}
```

```java
//修改员工，跳转列表页面
@PutMapping("/emp")
public String update(EmpVO empVO) {
    Emp emp = new Emp();
    BeanUtils.copyProperties(empVO, emp);
    emp.setCityId(empVO.getCity().getId());
    int update = empMapper.updateById(emp);
    log.info("update: {}", JSON.toJSONString(emp, true));

    return "redirect:/crud/list";
}
```

>删除（1）：表单提交删除 `POST 转 DELETE`

```html
<a href="#" onclick="deleteByForm(this)" th:attr="url=@{/crud/}+${emp.id}">表单删除</a>
```

```html
<form id="deleteForm" method="post" action="#">
    <input type="hidden" name="_method" value="DELETE">
</form>
```

```javascript
function deleteByForm(e) {
    //$(e).attr('url')      --> 获取url属性
    //$(e).attr('url', xxx) --> 为url属性赋值
    $('#deleteForm').attr('action', $(e).attr('url')).submit();

    return false; //取消<a>的默认行为
}
```

```java
//删除员工，跳转列表页面
@DeleteMapping("/{id}")
public String delete(@PathVariable Long id) {
    int delete = empMapper.deleteById(id);

    return "redirect:/crud/list";
}
```

> 删除（2）：ajax异步删除

```html
<a href="#" onclick="deleteByAjax(this)" th:attr="url=@{/crud/}+${emp.id}">ajax删除</a>
```

```javascript
function deleteByAjax(e) {
    $.ajax({
        type: 'delete',
        url: $(e).attr('url'),
        dataType: 'text',
        success: function (data) {
            //e表示<a>, parent表示<td>, 再parent表示<tr>
            $(e).parent().parent().remove();
            alert(data);
        }
    });
    return false;
}
```

```java
//ajax异步删除员工
@ResponseBody
@DeleteMapping("/{id}")
public String delete(@PathVariable Long id) {
    int delete = empMapper.deleteById(id);

    return "SUCCESS";
}
```
## 前台页面

> 存放于 `\templates\emps\` 的 `list.html + emp.html`

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>列表页面</title>

    <script th:src="@{/webjars/jquery/jquery.min.js}"></script>
    <script th:src="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
    <link th:href="@{/webjars/bootstrap/css/bootstrap.min.css}"/>

    <script>/*单独叙述*/</script>
</head>
<body>
<table>
    <tr>
        <th>姓名</th>
        <th>年龄</th>
        <th>城市</th>
        <th>操作</th>
    </tr>
    <tr th:if="${null==emps || 0==emps.size()}">
        <td colspan="4" th:text="员工列表为空"></td>
    </tr>
    <tr th:each="emp:${emps}" th:object="${emp}"> <!--th:object 和 *{...} 配合使用-->
        <td th:text="${emp.name}"></td>
        <td th:text="*{gender}?'男':'女'"></td>
        <td th:text="*{city.name}"></td>
        <td>
            <a th:href="@{/crud/}+*{id}">修改</a> <!--路径拼接-->
            <a href="#" onclick="deleteByForm(this)" th:attr="url=@{/crud/}+${emp.id}">删除</a>
            <a href="#" onclick="deleteByAjax(this)" th:attr="url=@{/crud/}+${emp.id}">删除</a>
        </td>
    </tr>
</table>
<a th:href="@{/crud/emp}">新增员工</a>

<form id="deleteForm" method="post" action="#">
    <input type="hidden" name="_method" value="DELETE">
</form>
</body>
</html>
```

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>员工信息页</title>
</head>
<body>
<form method="post" th:action="@{/crud/emp}">
    <!--新增和修改使用同一页面，区分方式：回显 emp 是否为空 ${null!=person}-->
    <input type="hidden" name="_method" value="put" th:if="${null!=emp}">
    <!--修改：PUT请求 + emp.id-->
    <input type="hidden" name="id" th:value="${emp.id}" th:if="${null!=emp}">

    <table>
        <tr>
            <td>姓名：</td>
            <td><input type="text" name="name" th:value="${null!=emp}?${emp.name}"></td>
        </tr>
        <tr>
            <td>性别：</td>
            <td>
                <!--th:checked radio标签是否选中-->
                <input type="radio" name="gender" value="1" th:checked="${null!=emp}?${1==emp.gender}">男
                <input type="radio" name="gender" value="0" th:checked="${null!=emp}?${1!=emp.gender}">女
            </td>
        </tr>
        <tr>
            <td>住址：</td>
            <td>
                <select name="city.id">
                    <!--th:selected 回显emp.city.id == 遍历city.id，则选中-->
                    <option th:each="city:${citys}" th:object="${city}" th:value="*{id}" th:text="*{name}"
                            th:selected="${null!=emp}?${emp.city.id}==*{id}"></option>
                </select>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <!--回显 emp 为空，则显示'新增'；否则显示'修改'-->
                <input type="submit" th:value="${null==emp}?'新增':'修改'">
            </td>
        </tr>
    </table>
</form>
</body>
</html>
```
# ajax

## 定义规范

> CRUD请求定义规范

```sh
#RESTful规范中，资源必须采用资源的名词复数定义
获取成员列表        GET       /orgz/members
获取单个成员        GET       /orgz/members/120
创建成员           POST       /orgz/members
修改成员           PUT        /orgz/members/120
批量修改           PUT        /orgz/members
修改成员的部分属性   PATCH      /orgz/members/120
删除成员           DELETE     /orgz/members/120
```

>复杂查询请求定义规范

```sh
过滤           GET  /module/tickets?state=open
排序           GET  /module/tickets?sort=-priority
排序           GET  /module/tickets?sort=-priority,created_at
过滤、排序      GET  /module/tickets?state=closed&sort=-created_at
搜索、过滤、排序 GET  /module/tickets?q=return&state=open&sort=-priority,created_at

一般数据请求    GET  /module/tickets/recently_closed
指定返回列      GET  /module/tickets?fields=id,subject&state=open&sort=-updated_at
分页           GET  /cars?num=10&size=5
```

## 后台逻辑

> 员工相关

```java
@Slf4j
@RestController
@RequestMapping("/emp")
public class EmpController {

    @Autowired
    EmpMapper empMapper;

    @GetMapping("")
    public Result listEmpByPage(
        @RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
        @RequestParam(name = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Emp> empList = empMapper.listAll();
        PageInfo<Emp> pageInfo = new PageInfo<>(empList, 5);
        return Result.success(pageInfo); //员工分页
    }

    @PostMapping("")
    public Result addEmp(@Valid Emp emp, BindingResult result) {
        System.out.println("addEmp: " + emp);
        if (result.hasErrors()) { //失败有错
            Map<String, String> map = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                map.put(error.getField(), error.getDefaultMessage()); //校验失败字段 - 校验失败原因
            });
            return Result.fail(map);
        }

        int insert = empMapper.insert(emp);
        return Result.success(); //新增成功
    }

    @GetMapping("/check_name")
    public Result checkEmpName(@RequestParam("empName") String empName) {
        Integer count = empMapper.selectCount(new QueryWrapper<Emp>().eq("emp_name", empName));
        if (count > 0) {
            return Result.fail("用户名已存在");
        }
        return Result.success(); //员工名可用
    }

    @GetMapping("/{id}")
    public Result getEmpById(@PathVariable("id") Integer id) {
        Emp emp = empMapper.selectById(id);
        return Result.success(emp);
    }

    @PutMapping("/{empId}")
    public Result updateById(@RequestBody Emp emp, @PathVariable("empId") Integer empId) {
        emp.setEmpId(empId);
        int update = empMapper.updateById(emp);
        return Result.success();
    }

    @DeleteMapping("/{empIds}")
    public Result deleteByIds(@PathVariable("empIds") String empIds) {
        List<String> ids = Arrays.stream(empIds.split(","))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        int delete = empMapper.deleteBatchIds(ids);
        return Result.success();
    }
}
```

> 部门相关

```java
@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    DeptMapper deptMapper;

    @GetMapping("")
    public Result listDept() {
        List<Dept> deptList = deptMapper.selectList(null);
        return Result.success(deptList);
    }
}
```

## 前台页面

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>index</title>
    <link rel="stylesheet" href="../webjars/bootstrap/css/bootstrap.min.css"/>
</head>
<body>
<!-- 员工修改的模态框 -->
<div class="modal fade" id="modal_emp_update" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">员工修改</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">empName</label>
                        <div class="col-sm-10">
                            <p class="form-control-static" id="empName_update_static"></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">email</label>
                        <div class="col-sm-10">
                            <input type="text" name="email" class="form-control" id="email_update_input"
                                   placeholder="email@atguigu.com">
                            <span class="help-block"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">gender</label>
                        <div class="col-sm-10">
                            <label class="radio-inline">
                                <input type="radio" name="gender" id="gender1_update_input" value="true"
                                       checked="checked">男
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="gender" id="gender2_update_input" value="false"> 女
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">deptName</label>
                        <div class="col-sm-4">
                            <!-- 部门提交部门id即可 -->
                            <select class="form-control" name="deptId" title=""></select>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="emp_update_btn">更新</button>
            </div>
        </div>
    </div>
</div>

<!-- 员工添加的模态框 -->
<div class="modal fade" id="modal_emp_add" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">员工添加</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">empName</label>
                        <div class="col-sm-10">
                            <input type="text" name="empName" class="form-control" id="empName_add_input"
                                   placeholder="empName">
                            <span class="help-block"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">email</label>
                        <div class="col-sm-10">
                            <input type="text" name="email" class="form-control" id="email_add_input"
                                   placeholder="email@atguigu.com">
                            <span class="help-block"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">gender</label>
                        <div class="col-sm-10">
                            <label class="radio-inline">
                                <input type="radio" name="gender" id="gender1_add_input" value="true"
                                       checked="checked"> 男
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="gender" id="gender2_add_input" value="false"> 女
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">deptName</label>
                        <div class="col-sm-4">
                            <!-- 提交部门id即可 -->
                            <select class="form-control" name="deptId" title=""></select>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="emp_save_btn">保存</button>
            </div>
        </div>
    </div>
</div>


<!--主页面搭建-->
<div class="container">
    <!--标题-->
    <div class="row">
        <div class="col-md-12">
            <h1>SSM-CRUD</h1>
        </div>
    </div>
    <!--操作栏-->
    <div class="row">
        <div class="pull-right">
            <button type="button" class="btn btn-primary" id="emp_add_modal_btn">
                <span class="glyphicon glyphicon glyphicon-pencil" aria-hidden="true"></span> 新增
            </button>
            <button type="button" class="btn btn-danger" id="emp_delete_batch_btn">
                <span class="glyphicon glyphicon glyphicon-trash" aria-hidden="true"></span> 删除
            </button>
        </div>
    </div>
    <!--表格-->
    <div class="row">
        <div class="col-md-12">
            <table class="table table-hover" id="table_emps">
                <thead>
                <tr>
                    <th><input type="checkbox" id="check_all" title=""/></th>
                    <th>#</th>
                    <th>empName</th>
                    <th>gender</th>
                    <th>email</th>
                    <th>deptName</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>

        </div>
    </div>
    <!--分页-->
    <div class="row">
        <!--分页文字-->
        <div id="page_info_area" class="col-md-4"></div>
        <!--分页条-->
        <div class="col-md-4 pull-right" id="page_nav_area"></div>
    </div>
</div>

<!-- Placed at the end of the document so the pages load faster -->
<script src="../webjars/jquery/jquery.min.js"></script>
<script src="../webjars/bootstrap/js/bootstrap.min.js"></script>

<script>
    let totalPages, currentPage;
    $(function () { //页面加载完成后，请求分页数据
        to_page(1);
    });

    function to_page(pageNum) {
        $.ajax({
            type: "GET",
            url: "/crud/emp",
            // contentType: "application/json", //请求参数的格式
            data: "pageNum=" + pageNum,
            // dataType: "json",                //返回参数的格式
            success: function (result) {
                if (200 === result.code) {
                    let datas = result.datas;
                    build_emps_table(datas); //1、解析并显示员工数据
                    build_page_info(datas);  //2、解析并显示分页信息
                    build_page_nav(datas);   //3、解析显示分页条数据
                }
            }
        });
    }

    //解析并显示员工数据
    function build_emps_table(datas) {
        $("#table_emps tbody").empty(); //填充前，先清空

        let emps = datas.list;
        $.each(emps, function (index, item) { //遍历集合
            let checkBoxTd = $("<td><input type='checkbox' class='check_item'/></td>");
            let empIdTd = $("<td></td>").append(item.empId);
            let empNameTd = $("<td></td>").append(item.empName);
            let genderTd = $("<td></td>").append(item.gender ? "男" : "女");
            let emailTd = $("<td></td>").append(item.email);
            let deptNameTd = $("<td></td>").append(item.dept.deptName);
            let editBtn = $("<button></button>").addClass("btn btn-primary btn-sm edit_btn")
                .append($("<span><span/>").addClass("glyphicon glyphicon-pencil")).append(" 编辑");
            editBtn.attr("edit-id", item.empId); //为"编辑"按钮关联属性empId
            let deleteBtn = $("<button></button>").addClass("btn btn-danger btn-sm delete_btn")
                .append($("<span></span>").addClass("glyphicon glyphicon-trash")).append(" 删除");
            deleteBtn.attr("delete-id", item.empId); //关联属性
            let btnTd = $("<td></td>").append(editBtn).append(" ").append(deleteBtn);

            $("<tr></tr>").append(checkBoxTd)
                .append(empIdTd)
                .append(empNameTd)
                .append(genderTd)
                .append(emailTd)
                .append(deptNameTd)
                .append(btnTd)
                .appendTo("#table_emps tbody"); //组装为 tbody
        });
    }

    //解析并显示分页信息
    function build_page_info(datas) {
        let $pageInfoArea = $("#page_info_area");
        $pageInfoArea.empty(); //填充前，先清空

        totalPages = datas.pages;
        currentPage = datas.pageNum;
        $pageInfoArea.append("当前 " + currentPage + " 页，总 " + totalPages + " 页，总 " + datas.total + " 条记录");
    }

    //解析显示分页条数据
    function build_page_nav(datas) {
        $("#page_nav_area").empty(); //填充前，先清空

        let ul = $("<ul></ul>").addClass("pagination  pull-right");
        let firstPageLi = $("<li></li>").append($("<a></a>").append("首页").attr("href", "#"));
        let prePageLi = $("<li></li>").append(
            $("<a></a>").attr("aria-label", "Previous").append("&laquo;").append(
                $("<span></span>").attr("aria-hidden", true)
            ));
        if (false === datas.hasPreviousPage) { //是否还有上一页？
            firstPageLi.addClass("disabled");
            prePageLi.addClass("disabled");
        } else {
            firstPageLi.click(function () { //添加点击事件
                to_page(1);
            });
            prePageLi.click(function () {
                to_page(datas.pageNum - 1);
            });
        }
        let nextPageLi = $("<li></li>").append($("<a></a>").append("&raquo;"));
        let lastPageLi = $("<li></li>").append($("<a></a>").append("末页").attr("href", "#"));
        if (false === datas.hasNextPage) {
            nextPageLi.addClass("disabled");
            lastPageLi.addClass("disabled");
        } else {
            nextPageLi.click(function () {
                to_page(datas.pageNum + 1);
            });
            lastPageLi.click(function () {
                to_page(datas.pages);
            });
        }

        ul.append(firstPageLi).append(prePageLi);
        $.each(datas.navigatepageNums, function (index, item) {
            let pageNumLi = $("<li></li>").append($("<a></a>").attr("href", "#").append(item));
            if (datas.pageNum === item) {
                pageNumLi.addClass("active"); //激活当前显示的页码
            }
            pageNumLi.click(function () {
                to_page(item);
            });
            ul.append(pageNumLi);
        });
        ul.append(nextPageLi).append(lastPageLi);

        $("<nav></nav>").append(ul).appendTo("#page_nav_area");
    }

    //新增员工：弹出新增页面
    $("#emp_add_modal_btn").click(function () {
        reset_modal_emp_add_form("#modal_emp_add form"); //弹出模态框之前，清空表单数据
        build_dept_select("#modal_emp_add select"); //填充部门列表
        $("#modal_emp_add").modal({ //弹出模态框
            backdrop: "static" //点击背景，模态框不消失
        });
    });

    //新增员工：弹出新增界面前，清空表单数据
    function reset_modal_emp_add_form(ele) {
        $(ele)[0].reset();
        //清空表单样式
        $(ele).find("*").removeClass("has-error has-success");
        $(ele).find(".help-block").text("");
    }

    //新增员工：填充部门列表
    function build_dept_select(ele) {
        $(ele).empty(); //填充前，先清空

        $.ajax({
            method: "GET",
            url: "/crud/dept",
            dataType: "json",
            success: function (result) {
                let datas = result.datas;
                $.each(datas, function (index, item) { //填充部门下拉框
                    let deptOpt = $("<option></option>").append(item.deptName).attr("value", item.deptId);
                    deptOpt.appendTo(ele);
                })
            }
        });
    }

    //新增员工：点击保存
    $("#emp_save_btn").click(function () {
        if (!validate_add_form()) { //校验表单数据
            return false;
        }

        if ($(this).attr("ajax-validate") === "error") {
            return false; //TODO：姓名占用提示消失
        }

        $.ajax({
            method: "POST",
            url: "/crud/emp",
            data: $("#modal_emp_add form").serialize(),
            success: function (result) {
                console.log(result);
                if (200 === result.code) {
                    $("#modal_emp_add").modal('hide'); //关闭新增弹框
                    to_page(totalPages + 1); //最好加 1
                } else { //显示后台验证失败的原因
                    if (undefined !== result.datas.email) { //显示邮箱错误信息
                        show_validate_msg("#email_add_input", "error", result.datas.email);
                    }
                    if (undefined !== result.datas.empName) { //显示员工名字的错误信息
                        show_validate_msg("#empName_add_input", "error", result.datas.empName);
                    }
                }
            }
        });
    });

    //校验表单数据
    function validate_add_form() {
        //1、验证员工姓名
        let empName = $("#empName_add_input").val();
        let regName = /(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})/;
        if (regName.test(empName)) {
            show_validate_msg("#empName_add_input", true, "");
        } else {
            show_validate_msg("#empName_add_input", false, "用户名可以是2-5位中文或者6-16位英文和数字的组合");
            return false;
        }

        //2、验证邮箱
        let email = $("#email_add_input").val();
        let regEmail = /^([a-z0-9_.-]+)@([\da-z.-]+)\.([a-z.]{2,6})$/;
        if (regEmail.test(email)) {
            show_validate_msg("#email_add_input", true, "");
        } else {
            show_validate_msg("#email_add_input", false, "邮箱格式不正确");
            return false;
        }
        return true;
    }

    //显示校验结果的提示信息
    function show_validate_msg(ele, status, msg) {
        $(ele).parent().removeClass("has-success has-error"); //填充前，先清空

        $(ele).next("span").text("");
        if (status) {
            $(ele).parent().addClass("has-success");
            $(ele).next("span").text(msg);
        } else {
            $(ele).parent().addClass("has-error");
            $(ele).next("span").text(msg);
        }
    }

    //校验用户名是否可用
    $("#empName_add_input").change(function () {
        const empName = this.value;
        $.ajax({
            type: "GET",
            url: "/crud/emp/check_name",
            data: "empName=" + empName,
            success: function (result) {
                if (result.code === 200) {
                    show_validate_msg("#empName_add_input", "success", "用户名可用");
                    $("#emp_save_btn").attr("ajax-validate", "success");
                } else {
                    show_validate_msg("#empName_add_input", "error", result.msg);
                    $("#emp_save_btn").attr("ajax-validate", "error");
                }
            }
        });
    });

    /**
     * 修改员工信息：弹出修改页面
     * 编辑按钮创建之前就绑定了click，所以绑定不上。
     * (1).可以在创建按钮的时候绑定
     * (2)绑定点击.live()。但是jquery新版没有 live()，使用 on() 进行替代
     */
    $(document).on("click", ".edit_btn", function () {
        build_dept_select("#modal_emp_update select"); //1、查出部门信息，并显示部门列表
        get_emp_by_id($(this).attr("edit-id"));        //2、查出员工信息，显示员工信息
        $("#emp_update_btn").attr("edit-id", $(this).attr("edit-id")); //3、把员工的id传递给模态框的更新按钮
        $("#modal_emp_update").modal({ //弹出更新模态框
            backdrop: "static"
        });
    });

    //修改员工信息：获取当前员工信息
    function get_emp_by_id(id) {
        $.ajax({
            method: "GET",
            url: "/crud/emp/" + id,
            success: function (result) {
                let emp = result.datas;
                $("#empName_update_static").text(emp.empName);
                $("#email_update_input").val(emp.email);
                $("#modal_emp_update input[name=gender]").val([emp.gender]);
                $("#modal_emp_update select").val([emp.deptId]);
            }
        });
    }

    //修改员工信息：点击修改
    $("#emp_update_btn").click(function () {
        //1、验证邮箱是否合法
        let email = $("#email_update_input").val();
        let regEmail = /^([a-z0-9_.-]+)@([\da-z.-]+)\.([a-z.]{2,6})$/;
        if (!regEmail.test(email)) {
            show_validate_msg("#email_update_input", "error", "邮箱格式不正确");
            return false;
        } else {
            show_validate_msg("#email_update_input", "success", "");
        }
        //2、发送ajax请求保存更新的员工数据
        $.ajax({
            method: "PUT",
            url: "/crud/emp/" + $(this).attr("edit-id"),
            contentType: "application/json",
            data: form2json("#modal_emp_update form"),
            success: function (result) {
                $("#modal_emp_update").modal("hide"); //1、关闭对话框
                to_page(currentPage);                 //2、回到本页面
            }
        });
    });

    //form表单数据转成json
    function form2json(form) {
        let data = {};
        let formData = $(form).serializeArray();
        $.each(formData, function (index, item) {
            data[item.name] = item.value;
        });
        return JSON.stringify(data);
    }

    //单个删除
    $(document).on("click", ".delete_btn", function () {
        let empName = $(this).parents("tr").find("td:eq(2)").text();
        if (confirm("是否删除【" + empName + "】吗？")) { //弹框提示: 是否删除？
            $.ajax({
                method: "DELETE",
                url: "/crud/emp/" + $(this).attr("delete-id"),
                success: function (result) {
                    if (200 === result.code) {
                        to_page(currentPage);
                    }
                }
            })
        }
    });

    //全选/全不选
    $("#check_all").click(function () {
        //attr: 获取/设置 自定义属性的值
        //prop: 获取/设置 原生属性的值
        $(".check_item").prop("checked", $(this).prop("checked"));
        //console.log("date: " + new Date().getTime()); //1574318788250 获取当前时间戳
    });

    //单个条目点击: 保证所有条目选中，则全选按钮选中
    $(document).on("click", ".check_item", function () {
        let flag = $(".check_item:checked").length === $(".check_item").length;
        $("#check_all").prop("checked", flag);
    });

    //批量删除
    $("#emp_delete_batch_btn").click(function () {
        let empNames = "", empIds = "";
        $.each($(".check_item:checked"), function () {
            let parents = $(this).parents("tr");
            empNames += parents.find("td:eq(2)").text() + ",";
            empIds += parents.find("td:eq(1)").text() + ",";
        });
        empNames = empNames.substring(0, empNames.length - 1);
        empIds = empIds.substring(0, empIds.length - 1); //3,4,5
        if (confirm("是否删除【" + empNames + "】吗？")) {
            $.ajax({
                url: "/crud/emp/" + empIds,
                method: "DELETE",
                success: function (result) {
                    if (200 === result.code) {
                        to_page(currentPage);
                    }
                }
            });
        }
    });
</script>
</body>
</html>
```

