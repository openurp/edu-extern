[#ftl]
[@b.head/]
[@b.grid items=applies var="apply"]
    [@b.gridbar]
      bar.addItem("单个审核",action.single('auditForm'));
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="std.code" title="学号" width="13%"/]
        [@b.col property="std.name" title="姓名" width="11%"]
          ${apply.std.name}
        [/@]
        [@b.col property="subject.name" title="考试科目" width="20%"/]
        [@b.col property="scoreText" title="成绩" width="7%"]
          <span [#if apply.status!="通过"]style="color:red"[/#if]>
            ${(apply.scoreText)!}
          </span>
        [/@]
        [@b.col property="std.state.department.name" title="院系"  width="15%"/]
        [@b.col property="acquiredOn" title="获得日期" width="15%"]${(apply.acquiredOn)!"--"}[/@]
        [@b.col title="免修" sortable="false"]
          [#list apply.courses as c]${c.name}[#sep],[/#list]
        [/@]
        [@b.col property="status" title="状态"]${apply.status}[/@]
    [/@]
[/@]

[@b.foot/]
