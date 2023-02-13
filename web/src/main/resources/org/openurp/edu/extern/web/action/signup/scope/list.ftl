[#ftl]
[@b.head/]
[@b.toolbar title="校外考试学生报名范围"]
    bar.addBack();
[/@]
[@b.grid items=scopes var="scope" ]
    [@b.gridbar]
        bar.addItem("${b.text("action.add")}",action.add());
        bar.addItem("${b.text("action.edit")}",action.edit());
        bar.addItem("${b.text("action.delete")}",action.remove());
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="level.name" title="培养层次" width="15%"/]
        [@b.col property="grades" title="年级" width="25%"/]
        [@b.col property="included" title="包含" width="15%"/]
        [@b.col property="setting.subject.name" title="考试科目" width="30%"/]
        [@b.col title="学号" width="10%"][#if scope.codes??]${scope.codes?split(",")?size}[/#if][/@]
    [/@]
[/@]

[@b.foot/]
