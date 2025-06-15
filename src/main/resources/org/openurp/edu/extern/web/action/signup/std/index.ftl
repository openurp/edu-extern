[#ftl]
[@b.head/]
<div class="container-fluid">
<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#"><i class="fa-solid fa-feather"></i>校外考试报名</a>
    </div>
    [#if configs?size>0]
    <ul class="nav navbar-nav navbar-right">
        <li>
        [@b.form class="navbar-form navbar-left" role="search" action="!configs"]
            [@b.a class="btn btn-sm btn-info" href="!configs"]<i class="fas fa-plus"></i>报名[/@]
        [/@]
        </li>
    </ul>
    [/#if]
  </div>
</nav>

[@b.form name="stdOtherExamSignUpForm" action="!configs"]
  [@b.div style="margin-top:10px;text-align:center;font-weight:bold;"]校外考试报名记录[/@]
  [@b.grid items=signUps var="signUp" class="border-1px border-colored"]
    [@b.row]
      [@b.col title="报名科目"]${(signUp.certificate.name)!}[/@]
      [@b.col title="报名时间"]${(signUp.updatedAt?string("yyyy-MM-dd HH:mm:ss"))?if_exists}[/@]
      [@b.col title="准考证打印"]
        [#if signUp.examRoom??][@b.a target="_blank" href="!examCertificate?signup.id="+signUp.id]打印[/@][/#if]
      [/@]
    [/@]
  [/@]

  [@b.div style="margin-top:10px;text-align:center;font-weight:bold;" ]校外考试成绩[/@]
  [@b.grid items=grades var="grade" class="border-1px border-colored"]
    [@b.row]
      [@b.col title="学年学期"]${grade.semester.schoolYear}学年 ${grade.semester.name}学期[/@]
      [@b.col title="科目名称"]${(grade.certificate.name)!}[/@]
      [@b.col title="成绩"]${(grade.scoreText)!}[/@]
      [@b.col title="是否通过"]${(grade.passed?string("通过","不通过"))!}[/@]
      [@b.col title="证书号"]${(grade.certificateNo)!}[/@]
      [@b.col title="获得年月"]${(grade.acquiredIn?string('yyyy-MM'))!}[/@]
    [/@]
  [/@]
[/@]
</div>
[@b.foot/]
