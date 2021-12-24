[#ftl]
[@b.head/]
  [@b.grid items=configs var="config"]
      [@b.gridbar title="校外考试开关维护"]
          bar.addItem("${b.text("action.new")}",action.add());
          bar.addItem("${b.text("action.edit")}",action.edit());
          bar.addItem("${b.text("action.delete")}",action.remove());
          bar.addItem("科目维护","editSetting()");
          bar.addItem("学生范围","editScope()");
      [/@]
      [@b.row]
          [@b.boxcol/]
          [@b.col property="category.name" title="考试类型"  width="8%"/]
          [@b.col property="code" title="开关代码" width="8%"/]
          [@b.col property="name" title="开关名称" width="15%"]
              [@b.a title="查看详细信息" href="config!info?id=${(config.id)!}"] [#if config.prediction]<span class="badge badge-warning">预</span>[/#if] ${(config.name)!}[/@]
          [/@]
          [@b.col property="semester.id" title="学年学期" width="10%"]${config.semester.schoolYear}-${config.semester.name}[/@]
          [@b.col property="beginAt" title="开放时间"  width="25%"]${config.beginAt?string("yyyy-MM-dd HH:mm")}~${config.endAt?string("yyyy-MM-dd HH:mm")}[/@]
          [@b.col property="opened" title="是否开放" width="8%"]${config.opened?string("${b.text('common.yes')}","${b.text('common.no')}")}[/@]
          [@b.col title="科目"  width="21%"][#list config.subjects as s]${s.name}[#if s_has_next]&nbsp;[/#if][/#list] [/@]
      [/@]
  [/@]
[@b.form name="configListForm" action="!search"][/@]
<script>
    function editSetting(){
        var configIds=bg.input.getCheckBoxValues('config.id');
        if(configIds==''||configIds.indexOf(',')!=-1){
            alert("请仅选择一条记录!");
            return false;
        }
        bg.form.addInput(document.configListForm, "config.id",configIds);
        bg.form.submit(document.configListForm,"${b.url('setting!search')}");
    }
    function editScope(){
        var configIds=bg.input.getCheckBoxValues('config.id');
        if(configIds==''||configIds.indexOf(',')!=-1){
            alert("请仅选择一条记录!");
            return false;
        }
        bg.form.addInput(document.configListForm, "config.id",configIds);
        bg.form.submit(document.configListForm,"${b.url('scope!search')}");
    }
</script>
[@b.foot/]
