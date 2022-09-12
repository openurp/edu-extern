[#ftl]
[@b.head/]
[@b.toolbar title="免修科目维护"]
    bar.addBack("${b.text("action.back")}");
    function clearNoNum(obj){
        obj.value = obj.value.replace(/[^\d.]/g,"");
        obj.value = obj.value.replace(/^\./g,"");
        obj.value = obj.value.replace(/\.{2,}/g,".");
        obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
    }
[/@]
[@b.form name="settingForm" action=b.rest.save(setting) theme="list"]
    [@b.select name="setting.subject.id" label="报考科目" required="true" style="width:150px" items=subjects?sort_by("name") empty="..."  value=setting.subject! /]
    [@b.textfield name="setting.minScore" label="最低分" value=setting.minScore! maxLength="10" required="false" style="width:150px"  comment="分"/]
    [@b.textfield name="setting.validMonths" label="有效月数" value=setting.validMonths! maxLength="10" required="false" style="width:150px" comment="以月为单位，不适用可以为空"/]
    [@b.select name="setting.auditDepart.id" label="审核部门" items=project.departments  value=setting.auditDepart! empty="..." required="true"/]
    [@b.select name="course.id" href=urp.api+"/base/edu/"+setting.config.project.id+"/courses.json?q={term}"  label="免修课程" style="width:400px" values=setting.courses multiple="true" empty="..." comment="可多个"/]
    [@b.textfield name="setting.remark" label="额外说明" value=setting.remark! maxLength="100" style="width:200px"/]
    [@b.formfoot]
        <input type="hidden" name="setting.config.id" value="${(setting.config.id)!}"/>
        <input type="hidden" name="config.id" value="${(setting.config.id)!}"/>
        [@b.submit value="action.submit"/]
        [@b.reset/]
    [/@]
[/@]
[@b.foot/]
