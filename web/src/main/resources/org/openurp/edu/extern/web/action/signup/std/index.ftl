[#ftl]
[@b.head/]
<div class="container">
<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><i class="fas fa-graduation-cap"></i>校外考试报名</a>
    </div>
    <ul class="nav navbar-nav navbar-right">
        <li>
        [@b.form class="navbar-form navbar-left" role="search" action="!configs"]
            [@b.a class="btn btn-sm btn-info" href="!configs"]<i class="fas fa-plus"></i>报名[/@]
        [/@]
        </li>
    </ul>
    </div>
</nav>

[@b.form name="stdOtherExamSignUpForm" action="!configs"]
  [@b.div style="margin-top:10px;text-align:center;font-weight:bold;"]校外考试报名记录[/@]
  [@b.grid items=signUps var="signUp" ]
    [@b.row]
      [@b.col title="报名科目"]${(signUp.subject.name)!}[/@]
      [@b.col title="报名时间"]${(signUp.updatedAt?string("yyyy-MM-dd HH:mm:ss"))?if_exists}[/@]
      [@b.col title="准考证打印"]
        [#if signUp.examRoom??][@b.a target="_blank" href="!examCertificate?signup.id="+signUp.id]打印[/@][/#if]
      [/@]
    [/@]
  [/@]
  [@b.div style="margin-top:10px;text-align:center;font-weight:bold;"]校外考试成绩[/@]
  [@b.grid items=grades var="grade"]
      [@b.row]
      [@b.col title="科目名称"]${(grade.subject.name)!}[/@]
      [@b.col title="成绩"]${(grade.scoreText)!}[/@]
      [@b.col title="证书号"]${(grade.certificate)!}[/@]
      [@b.col title="是否通过"]${(grade.passed?string("通过","不通过"))!}[/@]
      [@b.col title="获得日期"]${(grade.acquiredOn?string('yyyy-MM-dd'))!}[/@]
    [/@]
  [/@]
[/@]
</div>
[@b.foot/]
