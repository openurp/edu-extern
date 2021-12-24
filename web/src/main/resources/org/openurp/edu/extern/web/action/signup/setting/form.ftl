[#ftl]
[@b.head/]
[@b.toolbar title="考试科目维护"]
    bar.addBack("${b.text("action.back")}");
    function clearNoNum(obj){
        obj.value = obj.value.replace(/[^\d.]/g,"");
        obj.value = obj.value.replace(/^\./g,"");
        obj.value = obj.value.replace(/\.{2,}/g,".");
        obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
    }
[/@]
[@b.form name="settingForm" action=b.rest.save(setting) theme="list"]
    [@b.select name="setting.subject.id" label="报考科目" required="true" style="width:150px" items=subjects?sort_by("name") empty="..."  value=(setting.subject)?if_exists/]
    [@b.textfield name="setting.feeOfSignup" label="报名费" value=setting.feeOfSignup! maxLength="10" required="true" style="width:150px" onBlur="clearNoNum(this)" comment="分"/]
    [@b.textfield name="setting.feeOfMaterial" label="材料费" value=setting.feeOfMaterial! maxLength="10" required="true" style="width:150px" onBlur="clearNoNum(this)" comment="分"/]
    [@b.textfield name="setting.feeOfOutline" label="考纲费" value=setting.feeOfOutline! maxLength="10" required="true" style="width:150px" onBlur="clearNoNum(this)" comment="分"/]
    [@b.datepicker label="考试日期"  name="setting.examOn" required="false" value=setting.examOn! style="width:150px"/]
    [@b.datepicker label="开始时间" name="setting.examBeginAt" id="sTime" maxDate="#F{$dp.$D(\\'eTime\\')}" value="${(setting.examBeginAt)!}" format="HH:mm" required="false" /]
    [@b.datepicker label="结束时间" name="setting.examEndAt" id="eTime" minDate="#F{$dp.$D(\\'sTime\\')}" value="${(setting.examEndAt)!}" format="HH:mm" required="false" /]
    [@b.radios name="setting.reExamAllowed"  label="是否能够重考" value=(setting.reExamAllowed)!false?string('1','0') /]
    [@b.select name="setting.dependsOn.id" label="必须通过的科目" style="width:150px" value=setting.dependsOn! items=dependsOn empty="..."/]
    [@b.textfield name="setting.maxStd" label="最大报名人数" value=setting.maxStd maxLength="4" required="true" style="width:150px" comment="不限制人数可填-1"/]

    [@b.formfoot]
        <input type="hidden" name="setting.config.id" value="${(setting.config.id)!}"/>
        <input type="hidden" name="config.id" value="${(setting.config.id)!}"/>
        [@b.submit value="action.submit"/]
        [@b.reset/]
    [/@]
[/@]
[@b.foot/]
