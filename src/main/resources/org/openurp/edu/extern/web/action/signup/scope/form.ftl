[#ftl]
[@b.head/]
[@b.toolbar title="校外考试学生报名范围"]
    bar.addBack("${b.text("action.back")}");
[/@]
[@b.form name="scopeForm" action=b.rest.save(scope) theme="list"]
    [@b.radios name="scope.included" label="是否包含" value=scope.included required="true" /]
    [@b.select name="scope.level.id" label="培养层次" items=levels  value=scope.level required="true"/]
    [@b.select name="scope.setting.id" label="报考科目" required="true" items=config.settings option=r"${item.certificate.name}" /]
    [@b.textfield name="scope.grades" label="年级" value="${(scope.grades)!}" maxLength="50" required="false" style="width:300px" comment="多个年级使用半角逗号隔开"/]
    [@b.textarea name="scope.codes" required="false" maxlength="90000" label="学号" value=scope.codes! cols="80" rows="4" comment="多个学号使用半角逗号隔开"/]
    [@b.formfoot]
        <input type="hidden" name="config.id" value="${Parameters['config.id']}"/>
        [@b.reset/]
        [@b.submit value="action.submit"/]

    [/@]
[/@]

[@b.foot/]
