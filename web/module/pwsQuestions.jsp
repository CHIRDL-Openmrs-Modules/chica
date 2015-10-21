<div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt1_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                           <div class="questionStem">
                               <c:out value="${Prompt1_Text}"/>
                           </div>
                           <div class="answerContainer">
                               <div class="answerCheckbox">
                                   <c:choose>
                                     <c:when test="${empty Answer1_1}">
                                         <input type="checkbox" name="sub_Choice1" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="1"/><c:out value="${Answer1_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                               </div>
                               <div class="answerCheckbox">
                                   <c:choose>
                                     <c:when test="${empty Answer1_3}">
                                         <input type="checkbox" name="sub_Choice1" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="3"/><c:out value="${Answer1_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                               </div>
                               <div class="answerCheckbox">
                                   <c:choose>
                                     <c:when test="${empty Answer1_5}">
                                         <input type="checkbox" name="sub_Choice1" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="5"/><c:out value="${Answer1_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                               </div>
                           </div>
                           <div class="answerContainer">
                               <div class="answerCheckbox">
                                   <c:choose>
                                     <c:when test="${empty Answer1_2}">
                                         <input type="checkbox" name="sub_Choice1" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="2"/><c:out value="${Answer1_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                               </div>
                               <div class="answerCheckbox">
                                   <c:choose>
                                     <c:when test="${empty Answer1_4}">
                                         <input type="checkbox" name="sub_Choice1" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="4"/><c:out value="${Answer1_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                               </div>
                               <div class="answerCheckbox">
                                   <c:choose>
                                     <c:when test="${empty Answer1_6}">
                                         <input type="checkbox" name="sub_Choice1" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice1" value="6"/><c:out value="${Answer1_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                               </div>
                           </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt2_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
                                <c:out value="${Prompt2_Text}"/>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_1}">
                                         <input type="checkbox" name="sub_Choice2" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="1"/><c:out value="${Answer2_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_3}">
                                         <input type="checkbox" name="sub_Choice2" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="3"/><c:out value="${Answer2_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_5}">
                                         <input type="checkbox" name="sub_Choice2" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="5"/><c:out value="${Answer2_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_2}">
                                         <input type="checkbox" name="sub_Choice2" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="2"/><c:out value="${Answer2_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_4}">
                                         <input type="checkbox" name="sub_Choice2" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="4"/><c:out value="${Answer2_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer2_6}">
                                         <input type="checkbox" name="sub_Choice2" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice2" value="6"/><c:out value="${Answer2_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt3_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
                                <c:out value="${Prompt3_Text}"/>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_1}">
                                         <input type="checkbox" name="sub_Choice3" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="1"/><c:out value="${Answer3_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_3}">
                                         <input type="checkbox" name="sub_Choice3" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="3"/><c:out value="${Answer3_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_5}">
                                         <input type="checkbox" name="sub_Choice3" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="5"/><c:out value="${Answer3_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_2}">
                                         <input type="checkbox" name="sub_Choice3" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="2"/><c:out value="${Answer3_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_4}">
                                         <input type="checkbox" name="sub_Choice3" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="4"/><c:out value="${Answer3_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer3_6}">
                                         <input type="checkbox" name="sub_Choice3" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice3" value="6"/><c:out value="${Answer3_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt4_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
                                <c:out value="${Prompt4_Text}"/>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_1}">
                                         <input type="checkbox" name="sub_Choice4" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="1"/><c:out value="${Answer4_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_3}">
                                         <input type="checkbox" name="sub_Choice4" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="3"/><c:out value="${Answer4_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_5}">
                                         <input type="checkbox" name="sub_Choice4" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="5"/><c:out value="${Answer4_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_2}">
                                         <input type="checkbox" name="sub_Choice4" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="2"/><c:out value="${Answer4_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_4}">
                                         <input type="checkbox" name="sub_Choice4" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="4"/><c:out value="${Answer4_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer4_6}">
                                         <input type="checkbox" name="sub_Choice4" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice4" value="6"/><c:out value="${Answer4_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt5_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
                               <c:out value="${Prompt5_Text}"/>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_1}">
                                         <input type="checkbox" name="sub_Choice5" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="1"/><c:out value="${Answer5_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_3}">
                                         <input type="checkbox" name="sub_Choice5" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="3"/><c:out value="${Answer5_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_5}">
                                         <input type="checkbox" name="sub_Choice5" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="5"/><c:out value="${Answer5_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_2}">
                                         <input type="checkbox" name="sub_Choice5" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="2"/><c:out value="${Answer5_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_4}">
                                         <input type="checkbox" name="sub_Choice5" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="4"/><c:out value="${Answer5_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer5_6}">
                                         <input type="checkbox" name="sub_Choice5" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice5" value="6"/><c:out value="${Answer5_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="questionContainer">
                    <c:choose>
                        <c:when test="${empty Prompt6_Text}">
                            &nbsp;
                        </c:when>
                        <c:otherwise>
                            <div class="questionStem">
                                <c:out value="${Prompt6_Text}"/>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_1}">
                                         <input type="checkbox" name="sub_Choice6" value="1" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="1"/><c:out value="${Answer6_1}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_3}">
                                         <input type="checkbox" name="sub_Choice6" value="3" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="3"/><c:out value="${Answer6_3}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_5}">
                                         <input type="checkbox" name="sub_Choice6" value="5" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="5"/><c:out value="${Answer6_5}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                            <div class="answerContainer">
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_2}">
                                         <input type="checkbox" name="sub_Choice6" value="2" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="2"/><c:out value="${Answer6_2}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_4}">
                                         <input type="checkbox" name="sub_Choice6" value="4" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="4"/><c:out value="${Answer6_4}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                                <div class="answerCheckbox">
                                    <c:choose>
                                     <c:when test="${empty Answer6_6}">
                                         <input type="checkbox" name="sub_Choice6" value="6" disabled/><br/>
                                     </c:when>
                                     <c:otherwise>
                                         <input type="checkbox" name="sub_Choice6" value="6"/><c:out value="${Answer6_6}"/><br/>
                                     </c:otherwise>
                                   </c:choose>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>