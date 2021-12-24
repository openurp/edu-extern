[#ftl]
[@b.head/]
	[@b.toolbar title="校外考试报名(第二步)"]
		 bar.addBack("${b.text("action.back")}");
	[/@]	
<table >
  <thead>
  <br/>
  	 <p align="center"><font size="5"><b>报考须知</b></font></p>
  </thead>
  <tbody>
	<tr>
	    <td>
			<table border="0"><tr><td style="width:100px;min-height:200px"></td></tr></table>
		</td>
		<td>
			<pre>
			<font size="2">
					${config.notice?default("")}
			</font>
			</pre>
		</td>
	</tr>
 </tbody>
</table>
[@b.form name="actionForm" method="post" action="!signUpForm"]
		<table class="table" width="90%" align="center">
			<input type="hidden" name="config.id" value="${config.id}"/>
			<input type="hidden" name="setting.id" value="${Parameters['setting.id']}"/>
		<tr>
			  <td align="center">
				  <button class="btn btn-danger btn-sm" onclick="history.back();return false;">取消报名</button>
			    <button class="btn btn-primary btn-sm" onclick="bg.form.submit(this.form);return false;">我已知晓,下一步</button>
			  </td>
		</tr>
		</table>
[/@]
[@b.foot/]
