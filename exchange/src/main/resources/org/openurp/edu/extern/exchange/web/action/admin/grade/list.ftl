[#ftl]
[@b.head/]
  [@b.grid items=exchangeGrades var="exchangeGrade"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("成绩认定", action.single("convertList"), "action-update");
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));
      [#if exchangeGrades.totalItems gt 10000]
        var bar1=bar.addMenu("导出", function() {
          alert("导出数据每次不能超过10000条，建议分批导出。");
        });
        bar1.addItem("学分银行", function() {
          alert("导出数据每次不能超过10000条，建议分批导出。");
        }, "excel.png");
      [#else]
        var bar1=bar.addMenu("导出", action.exportData("exchangeStudent.std.user.code:学号,exchangeStudent.std.user.name:姓名,exchangeStudent.school.name:校外学校,exchangeStudent.level.name:培养层次,exchangeStudent.category.name:教育类别,exchangeStudent.majorName:外校专业,courseName:外校课程,scoreText:外校得分,credits:外校学分,acquiredOn:获得日期,updatedAt:录入时间"));
        bar1.addItem("学分银行", action.exportData("exchangeStudent.std.user.name:姓名,exchangeStudent.std.person.code:身份证号,exchangeStudent.school.code:转换学校代码,courseCode:原课程来源代码,exchangeStudent.majorName:原专业名称,courseName:原课程名称,exchangeStudent.school.name:原办学机构,exchangeStudent.level.code:原教育层次代码,exchangeStudent.category.code:原教育类别代码,credits:原学分,creditHours:原学时,scoreText:原成绩,acquiredOn:获得时间,exchangeStudent.std.level.code:转换后教育层次代码,courseCodes:转换后课程代码,courseNames:转换后课程名称,courseCredits:转换后学分", "xls", "fileName=学分银行成绩-外校成绩"), "excel.png");
      [/#if]
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="exchangeStudent.std.user.code"  width="13%"/]
      [@b.col title="姓名" property="exchangeStudent.std.user.name" width="10%"/]
      [@b.col title="专业" property="exchangeStudent.majorName"  width="15%"]
         <span title="${exchangeGrade.exchangeStudent.school.name}">${exchangeGrade.exchangeStudent.majorName}</span>
      [/@]
      [@b.col title="课程" property="courseName" width="20%"/]
      [@b.col title="得分" property="scoreText" width="5%"/]
      [@b.col title="学分" property="credits" width="5%"/]
      [@b.col title="获得日期" property="acquiredOn" width="7%"]${exchangeGrade.acquiredOn?string("yyyy-MM")}[/@]
      [@b.col title="免修课程" sortable="false" width="25%"]
        [#if exchangeGrade.courses?size >0 ]
        [#list exchangeGrade.courses as c]${c.name} ${c.credits}分 [#if c_has_next]<br>[/#if][/#list]
        [#else]--[/#if]
      [/@]
    [/@]
  [/@]
[@b.foot/]
