[#ftl]
[@b.head/]
[@b.toolbar title="证书成绩管理" /]
<div class="search-container">
  <div class="search-panel">
            [@b.form name="certificateGradesearchForm" action="!search" title="ui.searchForm" target="certificateGradeList" theme="search"]
                <input type="hidden" name="orderBy" value="certificateGrade.updatedAt desc"/>
                [@b.textfield name="certificateGrade.std.user.code" label="学号"/]
                [@b.textfield name="certificateGrade.std.user.name" label="姓名"/]
                [@b.textfield name="certificateGrade.std.state.grade" label="年级"/]
                [@b.select name="certificateGrade.std.state.department.id" label="院系" items=departments?sortBy(["code"]) empty="..." /]
                [@b.textfield name="certificateGrade.std.state.squad.name" label="班级名称"/]
                [@b.select name="certificateGrade.subject.category.id" id="categoryId" label="考试类型" items=certificateCategories empty="..."/]
                [@b.select name="certificateGrade.subject.id" id="subjectId" label="科目" items=certificateSubjects empty="..." /]
                [@b.field label="分数区间"]<input name="from" value="" maxLength="5" onBlur="clearNoNum(this)" style="width:48px;"/>-<input name="to" onBlur="clearNoNum(this)" value="" maxLength="5" style="width:48px;"/>
                [/@]
                [@b.textfield name="certificateGrade.examNo" label="准考证号"/]
                [@b.textfield name="certificateGrade.certificate" label="证书编号"/]
                [@b.select name="certificateGrade.passed" label="是否合格" items={"1":"合格", "0":"不合格"} empty="..." /]
                [@b.datepicker label="考试日期" name="certificateGrade.acquiredOn"/]
                [@b.datepicker id="fromAt" label="录入从" name="fromAt" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'toAt\\')}"/]
                [@b.datepicker id="toAt" label="录入到" name="toAt" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'fromAt\\')}"/]
                [#--
                [@b.datepicker id="convertFromAt" label="认定从" name="convertFromAt" format="yyyy-MM-dd" maxDate="#F{$dp.$D(\\'convertToAt\\')}"/]
                [@b.datepicker id="convertToAt" label="认定到" name="convertToAt" format="yyyy-MM-dd" minDate="#F{$dp.$D(\\'convertFromAt\\')}"/]
                --]
                [@b.select label="是否认定" name="hasCourse" items={ "1": "是", "0": "否" } empty="..."/]
            [/@]

            [@b.form name="importForm" action="!importForm" target="certificateGradeList"/]
  </div>
  <div class="search-list">
            [@b.div id="certificateGradeList" href="!search" /]
  </div>
</div>
<script>
    function importForm(){
        var form = document.importForm;
        bg.form.addInput(form,"importTitle","证书成绩导入");
        bg.form.addInput(form,"display","证书成绩导入模板");
        bg.form.submit(form);
    }

    //打印成绩
    function printted(){
        var certificateGradeIds = bg.input.getCheckBoxValues("certificateGrade.id");
        var form = action.getForm();
        if (certificateGradeIds) {
            bg.form.addInput(form,"certificateGradeIds",certificateGradeIds);
        }else{
            if(!confirm("是否打印查询条件内的所有数据?")) return;
                if(""!=action.page.paramstr){
                  bg.form.addHiddens(form,action.page.paramstr);
                  bg.form.addParamsInput(form,action.page.paramstr);
                }
            bg.form.addInput(form,"certificateGradeIds","");
        }
       [#-- bg.form.submit(form,"${b.url('!printShow')}","_blank");--]
    }

    function clearNoNum(obj){
        obj.value = obj.value.replace(/[^\d.]/g,"");
        obj.value = obj.value.replace(/^\./g,"");
        obj.value = obj.value.replace(/\.{2,}/g,".");
        obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
    }
</script>
[@b.foot/]
