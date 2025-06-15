[#ftl]
[@b.head/]
[@b.toolbar title="学生证书成绩查询"]
    bar.addBack("${b.text("action.back")}");
[/@]
<div class="search-container">
  <div class="search-panel">
      [@b.form name="certificateGradesearchForm" action="!search" title="ui.searchForm" target="certificateGradeList" theme="search"]
          <input type="hidden" name="orderBy" value="certificateGrade.updatedAt desc"/>
          [@base.semester name="certificateGrade.semester.id" label="学年学期" value=semester required="false"/]
          [@b.textfield name="certificateGrade.std.code" label="学号"/]
          [@b.textfield name="certificateGrade.std.name" label="姓名"/]
          [@b.textfield name="certificateGrade.std.state.grade" label="年级"/]
          [@b.select name="certificateGrade.std.state.department.id" label="院系" items=departments?sortBy(["code"]) empty="..." /]
          [@b.textfield name="certificateGrade.std.state.squad.name" label="班级名称"/]
          [@b.select name="certificateGrade.certificate.id" label="证书名称" items=certificates empty="..." /]
          [@b.field label="分数区间"]<input name="from" value="" maxLength="5" onBlur="clearNoNum(this)" style="width:48px;"/>-<input name="to" onBlur="clearNoNum(this)" value="" maxLength="5" style="width:48px;"/>
          [/@]
          [@b.textfield name="certificateGrade.examNo" label="准考证号"/]
          [@b.textfield name="certificateGrade.certificateNo" label="证书编号"/]
          [@b.select name="certificateGrade.passed" label="是否合格" items={"1":"合格", "0":"不合格"} empty="..." /]
          [@b.date label="获得年月" name="acquiredIn" format="yyyy-MM"/]
      [/@]
  </div>
  <div class="search-list">
      [@b.div id="certificateGradeList" href="!search?certificateGrade.semester.id=${semester.id}&orderBy=certificateGrade.updatedAt desc" /]
  </div>
</div>
<script>
    function clearNoNum(obj){
        obj.value = obj.value.replace(/[^\d.]/g,"");
        obj.value = obj.value.replace(/^\./g,"");
        obj.value = obj.value.replace(/\.{2,}/g,".");
        obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
    }
</script>
[@b.foot/]
