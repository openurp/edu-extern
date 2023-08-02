[@b.grid items=grades var="grade" sortable="false"]
    [@b.row]
        [@b.col property="subject.name" title="考试科目" width="25%"/]
        [@b.col property="scoreText" title="成绩" width="7%"]
          <span [#if !(grade.passed)]style="color:red"[/#if]>
            ${(grade.scoreText)!}
          </span>
        [/@]
        [@b.col property="certificate" title="证书编号"  width="15%"]${(grade.certificate)!"--"}[/@]
        [@b.col title="免修课程" sortable="false"][#list grade.exempts as c] ${c.code} ${c.name} ${c.getCredits(grade.std.level)}分[#sep]&nbsp;[/#list][/@]
        [@b.col property="updatedAt" title="免修时间"  width="12%"]${(grade.updatedAt?string("yy-MM-dd HH:mm"))!"--"}[/@]
    [/@]
[/@]