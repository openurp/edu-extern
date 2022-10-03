[#ftl]
[@b.head/]
  [@b.grid items=externGrades var="externGrade" id="extern_grades_grid"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("成绩认定", action.single("convertList"), "action-update");
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));
      [#if externGrades.totalItems gt 10000]
        bar.addItem("导出", function() {
          alert("导出数据每次不能超过10000条，建议分批导出。");
        });
      [#else]
        bar.addItem("导出", action.exportData("externStudent.std.code:学号,externStudent.std.name:姓名,externStudent.school.name:校外学校,externStudent.level.name:培养层次,externStudent.category.name:教育类别,externStudent.majorName:外校专业,courseName:外校课程,scoreText:外校得分,credits:外校学分,acquiredOn:获得日期,updatedAt:录入时间"));
      [/#if]
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="externStudent.std.code"  width="13%"/]
      [@b.col title="姓名" property="externStudent.std.name" width="10%"/]
      [@b.col title="专业" property="externStudent.majorName"  width="15%"]
         <span title="${externGrade.externStudent.school.name}" data-toggle="tooltip">${externGrade.externStudent.majorName}</span>
      [/@]
      [@b.col title="课程" property="courseName" width="20%"/]
      [@b.col title="得分" property="scoreText" width="5%"/]
      [@b.col title="学分" property="credits" width="5%"/]
      [@b.col title="获得日期" property="acquiredOn" width="7%"]${externGrade.acquiredOn?string("yyyy-MM")}[/@]
      [@b.col title="免修" sortable="false" width="25%"]
        [#if externGrade.courses?size >0 ]
        <span style="font-size:0.8em">[#list externGrade.courses as c]${c.name} ${c.credits}分 [#if c_has_next]<br>[/#if][/#list]</span>
        [#else]--[/#if]
      [/@]
    [/@]
  [/@]
  <script>
    $(function () {
      $('#externGrades [data-toggle="tooltip"]').tooltip()
    })
  </script>
[@b.foot/]
