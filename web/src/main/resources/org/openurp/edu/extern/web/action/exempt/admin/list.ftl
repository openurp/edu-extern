[#ftl]
[@b.head/]
[@b.grid items=applies var="apply"]
    [@b.gridbar]
      bar.addItem("单个审核",action.single('auditForm'));
      bar.addItem("批量同意",action.multi('audit',"确认审核通过？","&passed=1"));
      bar.addItem("批量驳回",action.multi('audit',"确认审核不通过，驳回到学生重修修改?","&passed=0"));
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="std.code" title="学号" width="12%"/]
        [@b.col property="std.name" title="姓名" width="10%"]
          <a href="javascript:void(0)" onclick="singleAudit(this)" title="点击进入审批">${apply.std.name}</a>
        [/@]
        [@b.col property="certificate.name" title="考试科目" width="18%"/]
        [@b.col property="scoreText" title="成绩" width="7%"]
          <span [#if apply.status!="通过"]style="color:red"[/#if]>
            ${(apply.scoreText)!}
          </span>
        [/@]
        [@b.col property="std.state.department.name" title="院系" width="10%"]
          ${apply.std.state.department.shortName!apply.std.state.department.name}
        [/@]
        [@b.col property="acquiredOn" title="获得日期" width="10%"]${(apply.acquiredOn)!"--"}[/@]
        [@b.col title="免修" sortable="false"]
          [#list apply.courses as c]${c.name}[#sep],[/#list]
        [/@]
        [@b.col property="status" title="状态" width="10%"]${apply.status}[/@]
    [/@]
[/@]
<script>
   function singleAudit(elem){
      jQuery(elem).parents("tr").children("td:first").children("input").prop("checked","checked")
      action.single("auditForm").func();
   }
</script>
[@b.foot/]
