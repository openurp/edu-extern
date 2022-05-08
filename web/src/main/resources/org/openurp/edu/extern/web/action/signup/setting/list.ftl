[#ftl]
[@b.head/]
[@b.toolbar title="考试科目维护"]
    bar.addBack();
[/@]
[@b.grid items=settings var="setting" ]
    [@b.gridbar]
        bar.addItem("${b.text("action.add")}",action.add());
        bar.addItem("${b.text("action.edit")}",action.edit());
        bar.addItem("${b.text("action.delete")}",action.remove());
        bar.addItem("批量修改","batchEdit()");
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="subject.name" title="报名科目" width="17%"/]
        [@b.col title="费用" width="10%"]
          [#if setting.feeOfSignup>0]报名费${setting.feeOfSignup/100.0}[/#if]
          [#if setting.feeOfMaterial>0]材料费${setting.feeOfMaterial/100.0}[/#if]
          [#if setting.feeOfOutline>0]考纲费${setting.feeOfOutline/100.0}[/#if]
        [/@]
        [@b.col property="maxStd" title="最大人数" width="7%"/]
        [@b.col title="条件" width="25%"][#list setting.scopes as con] ${con.includeIn?string('','限制:')}${(con.level.name)!} ${(con.grades)!('无')}[#if con_has_next]&nbsp;[/#if][/#list][/@]
        [@b.col property="reExamAllowed" title="能够重考" width="7%"]${(setting.reExamAllowed?default(false))?string("${b.text('common.yes')}","${b.text('common.no')}")}[/@]
        [@b.col property="dependsOn.name" title="须过科目" width="12%"/]
        [@b.col property="examOn" title="考试时间" width="15%"]
          [#if setting.examOn??]${setting.examOn?string("yyyy-MM-dd")}[/#if]
          [#if setting.examBeginAt?string!='00:00']${setting.examBeginAt}～${setting.examEndAt}[#else]&nbsp;[/#if]
        [/@]
    [/@]
[/@]

[@b.form name="settingPublicForm" action="!search"]
    <input type="hidden" name="setting.config.id" id="setting.config.id" value="${(config.id)!}"/>
[/@]

<script>
    function batchEdit(){
        document.settingPublicForm.action="${b.url('!batchEdit')}";
        var settingIds=bg.input.getCheckBoxValues("setting.id");
        if(settingIds==""){
            alert("请至少选择一个进行操作!");
            return false;
        }
        bg.form.addInput(document.settingPublicForm,"settingIds",settingIds);
        bg.form.submit(document.settingPublicForm);

    }
</script>
[@b.foot/]
