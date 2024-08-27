[#ftl]
[@b.head/]
[@b.toolbar title="免修科目维护"]
    bar.addBack();
[/@]
[@b.grid items=settings var="setting" ]
    [@b.gridbar]
        bar.addItem("${b.text("action.add")}",action.add());
        bar.addItem("${b.text("action.edit")}",action.edit());
        bar.addItem("${b.text("action.delete")}",action.remove());
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="certificate.name" title="免修科目" width="15%"/]
        [@b.col property="minScore" title="最低分" width="7%"/]
        [@b.col property="validMonths" title="有效月数" width="7%"/]
        [@b.col property="maxCount" title="冲抵门数上限" width="10%"/]
        [@b.col property="auditDepart.name" title="审核部门" width="10%"/]
        [@b.col title="免修课程"]
          [#list setting.courses?sort_by('code') as c]${c.code} ${c.name}[#sep]&nbsp;[/#list]
        [/@]
        [@b.col property="remark" title="备注" width="30%"]
          <span>${setting.remark!}<span>
        [/@]
    [/@]
[/@]

[@b.foot/]
