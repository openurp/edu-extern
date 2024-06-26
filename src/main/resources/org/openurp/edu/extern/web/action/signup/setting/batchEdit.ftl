[#ftl]
[@b.head/]
[@b.toolbar title="考试科目设置批量修改"]
    bar.addBack("${b.text("action.back")}");
    function clearNoNum(obj)
    {
        obj.value = obj.value.replace(/[^\d.]/g,"");
        obj.value = obj.value.replace(/^\./g,"");
        obj.value = obj.value.replace(/\.{2,}/g,".");
        obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
    }
[/@]
[@b.form name="settingListForm" action="!batchSave" theme="list"]
        <table class="grid-table" width="100%">
            <input type="hidden" name="setting.config.id" value="${config.id}"/>
            <input type="hidden" name="settingSize" value="${settings?size}"/>
            <thead class="grid-head">
              <tr>
                <td width="40%">报考科目名称</td>
                <td width="15%" id="f_settingFeeOfMaterial">材料费</td>
                <td width="15%" id="f_settingFeeOfSignup">报名费</td>
                <td width="15%" id="f_settingFeeOfOutline">考纲费</td>
                <td width="15%" id="f_settingMaxStd">最大学生数</td>
              </tr>
            </thead>
            [#list settings as setting]
            <tr>
                 <td style="text-align:center">${(setting.certificate.name)!}</td>
                 <td>
                      [@b.textfield label="" name="setting${setting_index}.feeOfMaterial"  maxLength="10" value="${(setting.feeOfMaterial)!}" request="true" style="width:75%" onBlur="clearNoNum(this)"/]
                 </td>
                 <td>
                     [@b.textfield label="" name="setting${setting_index}.feeOfSignup"  maxLength="10" value="${(setting.feeOfSignup)!}" style="width:75%" onBlur="clearNoNum(this)"/]
                 </td>
                 <td>
                      [@b.textfield label="" name="setting${setting_index}.feeOfOutline"  maxLength="10" value="${(setting.feeOfOutline)!}"  style="width:75%" onBlur="clearNoNum(this)"/]
                 </td>
                 <td>
                   [@b.textfield label="" name="setting${setting_index}.maxStd"  maxLength="10" value="${(setting.maxStd)!}"  style="width:75%" onBlur="clearNoNum(this)"/]
                 </td>
            </tr>
            <input type="hidden" name="setting${setting_index}.id" value="${setting.id?if_exists}"/>
            [/#list]
            <tr class="darkColumn">
                <td colspan="6" align="center">
                [@b.reset/]
                [@b.submit value="action.submit"/]
                </td>

            </tr>
        </table>
[/@]
[@b.foot/]
