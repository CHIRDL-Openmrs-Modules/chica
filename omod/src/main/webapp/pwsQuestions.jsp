<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<div class="pws_container">
    <header id="pws_prompts_header">
        <a href="">
            <h4 class="logo">Physician Prompts</h4>
        </a>
        <a href="#">
            <div class="ui-icon ui-icon-minusthick white"></div>
        </a>
    </header>
    <section class="pws_prompts" id="pws_prompts">
        <table width="1002" border="0" class="pws_prompts_table">
            <tbody>
                <tr>
                    <c:choose>
                        <c:when test="${empty Prompt1_Text}">
                            <td width="500" valign="top" class="pws_prompt_text">&nbsp;</td>
                        </c:when>
                        <c:otherwise>
                            <td width="500" valign="top" class="pws_prompt_text">
                                <c:out value="${Prompt1_Text}" />
                            </td>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${empty Prompt2_Text}">
                            <td width="500" valign="top" class="pws_prompt_text left_border">&nbsp;</td>
                        </c:when>
                        <c:otherwise>
                            <td width="500" valign="top" class="pws_prompt_text left_border">
                                <c:out value="${Prompt2_Text}" />
                            </td>
                        </c:otherwise>
                    </c:choose>
                </tr>
                <tr>
                    <td valign="top">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer1_1}">
                                                <input type="checkbox" name="sub_Choice1" value="1" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice1" id="Answer1_1" value="1" ${fn:contains(Choice1, '1') ? 'checked' : ''}/>
												<label for="Answer1_1"><c:out value="${Answer1_1}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer1_2}">
                                                <input type="checkbox" name="sub_Choice1" value="2" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice1" id="Answer1_2" value="2" ${fn:contains(Choice1, '2') ? 'checked' : ''}/>
												<label for="Answer1_2"><c:out value="${Answer1_2}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td valign="top" class="left_border">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer2_1}">
                                                <input type="checkbox" name="sub_Choice2" value="1" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice2" id="Answer2_1" value="1" ${fn:contains(Choice2, '1') ? 'checked' : ''}/>
												<label for="Answer2_1"><c:out value="${Answer2_1}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer2_2}">
                                                <input type="checkbox" name="sub_Choice2" value="2" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice2" id="Answer2_2" value="2" ${fn:contains(Choice2, '2') ? 'checked' : ''}/>
                                                <label for="Answer2_2"><c:out value="${Answer2_2}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer1_3}">
                                                <input type="checkbox" name="sub_Choice1" value="3" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice1" id="Answer1_3" value="3" ${fn:contains(Choice1, '3') ? 'checked' : ''}/>
                                                <label for="Answer1_3"><c:out value="${Answer1_3}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer1_4}">
                                                <input type="checkbox" name="sub_Choice1" value="4" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice1" id="Answer1_4" value="4" ${fn:contains(Choice1, '4') ? 'checked' : ''}/>
                                                <label for="Answer1_4"><c:out value="${Answer1_4}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td valign="top" class="left_border">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer2_3}">
                                                <input type="checkbox" name="sub_Choice2" value="3" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice2" id="Answer2_3" value="3" ${fn:contains(Choice2, '3') ? 'checked' : ''}/>
                                                <label for="Answer2_3"><c:out value="${Answer2_3}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer2_4}">
                                                <input type="checkbox" name="sub_Choice2" value="4" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice2" id="Answer2_4" value="4" ${fn:contains(Choice2, '4') ? 'checked' : ''}/>
                                                <label for="Answer2_4"><c:out value="${Answer2_4}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <table width="499" class="pws_leaves_table_padded">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer1_5}">
                                                <input type="checkbox" name="sub_Choice1" value="5" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice1" id="Answer1_5" value="5" ${fn:contains(Choice1, '5') ? 'checked' : ''}/>
                                                <label for="Answer1_5"><c:out value="${Answer1_5}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer1_6}">
                                                <input type="checkbox" name="sub_Choice1" value="6" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice1" id="Answer1_6" value="6" ${fn:contains(Choice1, '6') ? 'checked' : ''}/>
                                                <label for="Answer1_6"><c:out value="${Answer1_6}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td valign="top" class="left_border">
                        <table width="499" class="pws_leaves_table_padded">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer2_5}">
                                                <input type="checkbox" name="sub_Choice2" value="5" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice2" id="Answer2_5" value="5" ${fn:contains(Choice2, '5') ? 'checked' : ''}/>
                                                <label for="Answer2_5"><c:out value="${Answer2_5}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer2_6}">
                                                <input type="checkbox" name="sub_Choice2" value="6" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice2" id="Answer2_6" value="6" ${fn:contains(Choice2, '6') ? 'checked' : ''}/>
                                                <label for="Answer2_6"><c:out value="${Answer2_6}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </tbody>
        </table>
        <table width="1002" border="0" class="pws_prompts_table">
            <tbody>
                <tr>
                    <c:choose>
                        <c:when test="${empty Prompt3_Text}">
                            <td width="500" valign="top" class="pws_prompt_text">&nbsp;</td>
                        </c:when>
                        <c:otherwise>
                            <td width="500" valign="top" class="pws_prompt_text">
                                <c:out value="${Prompt3_Text}" />
                            </td>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${empty Prompt4_Text}">
                            <td width="500" valign="top" class="pws_prompt_text left_border">&nbsp;</td>
                        </c:when>
                        <c:otherwise>
                            <td width="500" valign="top" class="pws_prompt_text left_border">
                                <c:out value="${Prompt4_Text}" />
                            </td>
                        </c:otherwise>
                    </c:choose>
                </tr>
                <tr>
                    <td valign="top">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer3_1}">
                                                <input type="checkbox" name="sub_Choice3" value="1" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice3" id="Answer3_1" value="1" ${fn:contains(Choice3, '1') ? 'checked' : ''}/>
                                                <label for="Answer3_1"><c:out value="${Answer3_1}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer3_2}">
                                                <input type="checkbox" name="sub_Choice3" value="2" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice3" id="Answer3_2" value="2" ${fn:contains(Choice3, '2') ? 'checked' : ''}/>
                                                <label for="Answer3_2"><c:out value="${Answer3_2}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td valign="top" class="left_border">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer4_1}">
                                                <input type="checkbox" name="sub_Choice4" value="1" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice4" id="Answer4_1" value="1" ${fn:contains(Choice4, '1') ? 'checked' : ''}/>
                                                <label for="Answer4_1"><c:out value="${Answer4_1}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer4_2}">
                                                <input type="checkbox" name="sub_Choice4" value="2" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice4" id="Answer4_2" value="2" ${fn:contains(Choice4, '2') ? 'checked' : ''}/>
                                                <label for="Answer4_2"><c:out value="${Answer4_2}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer3_3}">
                                                <input type="checkbox" name="sub_Choice3" value="3" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice3" id="Answer3_3" value="3" ${fn:contains(Choice3, '3') ? 'checked' : ''}/>
                                                <label for="Answer3_3"><c:out value="${Answer3_3}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer3_4}">
                                                <input type="checkbox" name="sub_Choice3" value="4" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice3" id="Answer3_4" value="4" ${fn:contains(Choice3, '4') ? 'checked' : ''}/>
                                                <label for="Answer3_4"><c:out value="${Answer3_4}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td valign="top" class="left_border">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer4_3}">
                                                <input type="checkbox" name="sub_Choice4" value="3" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice4" id="Answer4_3" value="3" ${fn:contains(Choice4, '3') ? 'checked' : ''}/>
                                                <label for="Answer4_3"><c:out value="${Answer4_3}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer4_4}">
                                                <input type="checkbox" name="sub_Choice4" value="4" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice4" id="Answer4_4" value="4" ${fn:contains(Choice4, '4') ? 'checked' : ''}/>
                                                <label for="Answer4_4"><c:out value="${Answer4_4}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <table width="499" class="pws_leaves_table_padded">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer3_5}">
                                                <input type="checkbox" name="sub_Choice3" value="5" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice3" id="Answer3_5" value="5" ${fn:contains(Choice3, '5') ? 'checked' : ''}/>
                                                <label for="Answer3_5"><c:out value="${Answer3_5}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer3_6}">
                                                <input type="checkbox" name="sub_Choice3" value="6" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice3" id="Answer3_6" value="6" ${fn:contains(Choice3, '6') ? 'checked' : ''}/>
                                                <label for="Answer3_6"><c:out value="${Answer3_6}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td valign="top" class="left_border">
                        <table width="499" class="pws_leaves_table_padded">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer4_5}">
                                                <input type="checkbox" name="sub_Choice4" value="5" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice4" id="Answer4_5" value="5" ${fn:contains(Choice4, '5') ? 'checked' : ''}/>
                                                <label for="Answer4_5"><c:out value="${Answer4_5}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer4_6}">
                                                <input type="checkbox" name="sub_Choice4" value="6" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice4" id="Answer4_6" value="6" ${fn:contains(Choice4, '6') ? 'checked' : ''}/>
                                                <label for="Answer4_6"><c:out value="${Answer4_6}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </tbody>
        </table>
        <table width="1002" border="0" class="pws_prompts_table">
            <tbody>
                <tr>
                    <c:choose>
                        <c:when test="${empty Prompt5_Text}">
                            <td width="500" valign="top" class="pws_prompt_text">&nbsp;</td>
                        </c:when>
                        <c:otherwise>
                            <td width="500" valign="top" class="pws_prompt_text">
                                <c:out value="${Prompt5_Text}" />
                            </td>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${empty Prompt6_Text}">
                            <td width="500" valign="top" class="pws_prompt_text left_border">&nbsp;</td>
                        </c:when>
                        <c:otherwise>
                            <td width="500" valign="top" class="pws_prompt_text left_border">
                                <c:out value="${Prompt6_Text}" />
                            </td>
                        </c:otherwise>
                    </c:choose>
                </tr>
                <tr>
                    <td valign="top">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer5_1}">
                                                <input type="checkbox" name="sub_Choice5" value="1" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice5" id="Answer5_1" value="1" ${fn:contains(Choice5, '1') ? 'checked' : ''}/>
                                                <label for="Answer5_1"><c:out value="${Answer5_1}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer5_2}">
                                                <input type="checkbox" name="sub_Choice5" value="2" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice5" id="Answer5_2" value="2" ${fn:contains(Choice5, '2') ? 'checked' : ''}/>
                                                <label for="Answer5_2"><c:out value="${Answer5_2}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td valign="top" class="left_border">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer6_1}">
                                                <input type="checkbox" name="sub_Choice6" value="1" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice6" id="Answer6_1" value="1" ${fn:contains(Choice6, '1') ? 'checked' : ''}/>
                                                <label for="Answer6_1"><c:out value="${Answer6_1}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer6_2}">
                                                <input type="checkbox" name="sub_Choice6" value="2" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice6" id="Answer6_2" value="2" ${fn:contains(Choice6, '2') ? 'checked' : ''}/>
                                                <label for="Answer6_2"><c:out value="${Answer6_2}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer5_3}">
                                                <input type="checkbox" name="sub_Choice5" value="3" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice5" id="Answer5_3" value="3" ${fn:contains(Choice5, '3') ? 'checked' : ''}/>
                                                <label for="Answer5_3"><c:out value="${Answer5_3}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer5_4}">
                                                <input type="checkbox" name="sub_Choice5" value="4" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice5" id="Answer5_4" value="4" ${fn:contains(Choice5, '4') ? 'checked' : ''}/>
                                                <label for="Answer5_4"><c:out value="${Answer5_4}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td valign="top" class="left_border">
                        <table width="499" class="pws_leaves_table">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer6_3}">
                                                <input type="checkbox" name="sub_Choice6" value="3" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice6" id="Answer6_3" value="3" ${fn:contains(Choice6, '3') ? 'checked' : ''}/>
                                                <label for="Answer6_3"><c:out value="${Answer6_3}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer6_4}">
                                                <input type="checkbox" name="sub_Choice6" value="4" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice6" id="Answer6_4" value="4" ${fn:contains(Choice6, '4') ? 'checked' : ''}/>
                                                <label for="Answer6_4"><c:out value="${Answer6_4}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td valign="top">
                        <table width="499" class="pws_leaves_table_padded">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer5_5}">
                                                <input type="checkbox" name="sub_Choice5" value="5" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice5" id="Answer5_5" value="5" ${fn:contains(Choice5, '5') ? 'checked' : ''}/>
                                                <label for="Answer5_5"><c:out value="${Answer5_5}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer5_6}">
                                                <input type="checkbox" name="sub_Choice5" value="6" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice5" id="Answer5_6" value="6" ${fn:contains(Choice5, '6') ? 'checked' : ''}/>
                                                <label for="Answer5_6"><c:out value="${Answer5_6}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td valign="top" class="left_border">
                        <table width="499" class="pws_leaves_table_padded">
                            <tbody>
                                <tr>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer6_5}">
                                                <input type="checkbox" name="sub_Choice6" value="5" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice6" id="Answer6_5" value="5" ${fn:contains(Choice6, '5') ? 'checked' : ''}/>
                                                <label for="Answer6_5"><c:out value="${Answer6_5}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td width="249" valign="top" class="pws_leaf_cell">
                                        <c:choose>
                                            <c:when test="${empty Answer6_6}">
                                                <input type="checkbox" name="sub_Choice6" value="6" disabled/>
                                                <br/>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="checkbox" name="sub_Choice6" id="Answer6_6" value="6" ${fn:contains(Choice6, '6') ? 'checked' : ''}/>
                                                <label for="Answer6_6"><c:out value="${Answer6_6}" /></label>
                                                <br/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </tbody>
        </table>
    </section>
</div>