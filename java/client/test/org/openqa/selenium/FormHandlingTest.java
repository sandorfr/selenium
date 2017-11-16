// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;
import static org.openqa.selenium.testing.Driver.IE;
import static org.openqa.selenium.testing.Driver.MARIONETTE;
import static org.openqa.selenium.testing.Driver.PHANTOMJS;
import static org.openqa.selenium.testing.Driver.SAFARI;
import static org.openqa.selenium.testing.TestUtilities.catchThrowable;
import static org.openqa.selenium.testing.TestUtilities.isIe6;
import static org.openqa.selenium.testing.TestUtilities.isIe7;

import org.junit.Test;
import org.openqa.selenium.environment.webserver.Page;
import org.openqa.selenium.testing.Ignore;
import org.openqa.selenium.testing.JUnit4TestBase;
import org.openqa.selenium.testing.NotYetImplemented;
import org.openqa.selenium.testing.TestUtilities;

import java.io.File;
import java.io.IOException;

public class FormHandlingTest extends JUnit4TestBase {

  @Test
  public void testShouldClickOnSubmitInputElements() {
    driver.get(pages.formPage);
    driver.findElement(By.id("submitButton")).click();
    wait.until(titleIs("We Arrive Here"));
    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  @Test
  public void testClickingOnUnclickableElementsDoesNothing() {
    driver.get(pages.formPage);
    driver.findElement(By.xpath("//body")).click();
  }

  @Test
  public void testShouldBeAbleToClickImageButtons() {
    driver.get(pages.formPage);
    driver.findElement(By.id("imageButton")).click();
    wait.until(titleIs("We Arrive Here"));
    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  @Test
  public void testShouldBeAbleToSubmitForms() {
    driver.get(pages.formPage);
    driver.findElement(By.name("login")).submit();
    wait.until(titleIs("We Arrive Here"));
  }

  @Test
  public void testShouldSubmitAFormWhenAnyInputElementWithinThatFormIsSubmitted() {
    driver.get(pages.formPage);
    driver.findElement(By.id("checky")).submit();
    wait.until(titleIs("We Arrive Here"));
  }

  @Test
  public void testShouldSubmitAFormWhenAnyElementWithinThatFormIsSubmitted() {
    driver.get(pages.formPage);
    driver.findElement(By.xpath("//form/p")).submit();
    wait.until(titleIs("We Arrive Here"));
  }

  @Test
  @Ignore(PHANTOMJS)
  @Ignore(SAFARI)
  @NotYetImplemented(
    value = MARIONETTE, reason = "Delegates to JS and so the wrong exception is returned")
  public void testShouldNotBeAbleToSubmitAFormThatDoesNotExist() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.name("SearchableText"));
    Throwable t = catchThrowable(element::submit);
    assertThat(t, instanceOf(NoSuchElementException.class));
  }

  @Test
  public void testShouldBeAbleToEnterTextIntoATextAreaBySettingItsValue() {
    driver.get(pages.javascriptPage);
    WebElement textarea = driver.findElement(By.id("keyUpArea"));
    String cheesy = "brie and cheddar";
    textarea.sendKeys(cheesy);
    assertThat(textarea.getAttribute("value"), equalTo(cheesy));
  }

  @Test
  public void testSendKeysKeepsCapitalization() {
    driver.get(pages.javascriptPage);
    WebElement textarea = driver.findElement(By
                                                 .id("keyUpArea"));
    String cheesey = "BrIe And CheDdar";
    textarea.sendKeys(cheesey);
    assertThat(textarea.getAttribute("value"), equalTo(cheesey));
  }

  @Test
  @NotYetImplemented(value = MARIONETTE)
  public void testShouldSubmitAFormUsingTheNewlineLiteral() {
    driver.get(pages.formPage);
    WebElement nestedForm = driver.findElement(By.id("nested_form"));
    WebElement input = nestedForm.findElement(By.name("x"));
    input.sendKeys("\n");
    wait.until(titleIs("We Arrive Here"));
    assertTrue(driver.getCurrentUrl().endsWith("?x=name"));
  }

  @Test
  public void testShouldSubmitAFormUsingTheEnterKey() {
    driver.get(pages.formPage);
    WebElement nestedForm = driver.findElement(By.id("nested_form"));
    WebElement input = nestedForm.findElement(By.name("x"));
    input.sendKeys(Keys.ENTER);
    wait.until(titleIs("We Arrive Here"));
    assertTrue(driver.getCurrentUrl().endsWith("?x=name"));
  }

  @Test
  public void testShouldEnterDataIntoFormFields() {
    driver.get(pages.xhtmlTestPage);
    WebElement element = driver.findElement(By.xpath("//form[@name='someForm']/input[@id='username']"));
    String originalValue = element.getAttribute("value");
    assertThat(originalValue, equalTo("change"));

    element.clear();
    element.sendKeys("some text");

    element = driver.findElement(By.xpath("//form[@name='someForm']/input[@id='username']"));
    String newFormValue = element.getAttribute("value");
    assertThat(newFormValue, equalTo("some text"));
  }

  @Test
  @Ignore(value = SAFARI, reason = "issue 4220")
  public void testShouldBeAbleToAlterTheContentsOfAFileUploadInputElement() throws IOException {
    driver.get(pages.formPage);
    WebElement uploadElement = driver.findElement(By.id("upload"));
    assertThat(uploadElement.getAttribute("value"), equalTo(""));

    File file = File.createTempFile("test", "txt");
    file.deleteOnExit();

    uploadElement.sendKeys(file.getAbsolutePath());

    String uploadPath = uploadElement.getAttribute("value");
    assertTrue(uploadPath.endsWith(file.getName()));
  }

  @Test
  @Ignore(value = SAFARI, reason = "issue 4220")
  public void testShouldBeAbleToSendKeysToAFileUploadInputElementInAnXhtmlDocument()
      throws IOException {
    assumeFalse("IE before 9 doesn't handle pages served with an XHTML content type,"
                + " and just prompts for to download it",
                TestUtilities.isOldIe(driver));

    driver.get(pages.xhtmlFormPage);
    WebElement uploadElement = driver.findElement(By.id("file"));
    assertThat(uploadElement.getAttribute("value"), equalTo(""));

    File file = File.createTempFile("test", "txt");
    file.deleteOnExit();

    uploadElement.sendKeys(file.getAbsolutePath());

    String uploadPath = uploadElement.getAttribute("value");
    assertTrue(uploadPath.endsWith(file.getName()));
  }

  @Test
  @Ignore(value = SAFARI, reason = "issue 4220")
  public void testShouldBeAbleToUploadTheSameFileTwice() throws IOException {
    File file = File.createTempFile("test", "txt");
    file.deleteOnExit();

    driver.get(pages.formPage);
    WebElement uploadElement = driver.findElement(By.id("upload"));
    assertThat(uploadElement.getAttribute("value"), equalTo(""));

    uploadElement.sendKeys(file.getAbsolutePath());
    uploadElement.submit();

    driver.get(pages.formPage);
    uploadElement = driver.findElement(By.id("upload"));
    assertThat(uploadElement.getAttribute("value"), equalTo(""));

    uploadElement.sendKeys(file.getAbsolutePath());
    uploadElement.submit();

    // If we get this far, then we're all good.
  }

  @Test
  public void testSendingKeyboardEventsShouldAppendTextInInputs() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("working"));
    element.sendKeys("some");
    String value = element.getAttribute("value");
    assertThat(value, is("some"));

    element.sendKeys(" text");
    value = element.getAttribute("value");
    assertThat(value, is("some text"));
  }

  @Test
  public void testSendingKeyboardEventsShouldAppendTextInInputsWithExistingValue() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("inputWithText"));
    element.sendKeys(". Some text");
    String value = element.getAttribute("value");

    assertThat(value, is("Example text. Some text"));
  }

  @Test
  public void testSendingKeyboardEventsShouldAppendTextInTextAreas() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("withText"));

    element.sendKeys(". Some text");
    String value = element.getAttribute("value");

    assertThat(value, is("Example text. Some text"));
  }

  @Test
  public void testEmptyTextBoxesShouldReturnAnEmptyStringNotNull() {
    driver.get(pages.formPage);
    WebElement emptyTextBox = driver.findElement(By.id("working"));
    assertEquals(emptyTextBox.getAttribute("value"), "");
  }

  @Test
  @Ignore(PHANTOMJS)
  @Ignore(SAFARI)
  public void handleFormWithJavascriptAction() {
    String url = appServer.whereIs("form_handling_js_submit.html");
    driver.get(url);
    WebElement element = driver.findElement(By.id("theForm"));
    element.submit();
    Alert alert = wait.until(alertIsPresent());
    String text = alert.getText();
    alert.accept();

    assertEquals("Tasty cheese", text);
  }

  @Test
  @Ignore(SAFARI)
  public void testCanClickOnASubmitButton() {
    checkSubmitButton("internal_explicit_submit");
  }

  @Test
  @Ignore(SAFARI)
  public void testCanClickOnASubmitButtonNestedSpan() {
    checkSubmitButton("internal_span_submit");
  }

  @Test
  @Ignore(SAFARI)
  public void testCanClickOnAnImplicitSubmitButton() {
    assumeFalse(isIe6(driver) || isIe7(driver) );
    checkSubmitButton("internal_implicit_submit");
  }

  @Test
  @Ignore(IE)
  @Ignore(SAFARI)
  public void testCanClickOnAnExternalSubmitButton() {
    checkSubmitButton("external_explicit_submit");
  }

  @Test
  @Ignore(IE)
  @Ignore(SAFARI)
  public void testCanClickOnAnExternalImplicitSubmitButton() {
    checkSubmitButton("external_implicit_submit");
  }

  @Test
  public void canSubmitFormWithSubmitButtonIdEqualToSubmit() {
    String blank = appServer.create(new Page().withTitle("Submitted Successfully!"));
    driver.get(appServer.create(new Page().withBody(
        String.format("<form action='%s'>", blank),
        "  <input type='submit' id='submit' value='Submit'>",
        "</form>")));

    driver.findElement(By.id("submit")).submit();
    wait.until(titleIs("Submitted Successfully!"));
  }

  @Test
  public void canSubmitFormWithSubmitButtonNameEqualToSubmit() {
    String blank = appServer.create(new Page().withTitle("Submitted Successfully!"));
    driver.get(appServer.create(new Page().withBody(
        String.format("<form action='%s'>", blank),
        "  <input type='submit' name='submit' value='Submit'>",
        "</form>")));

    driver.findElement(By.name("submit")).submit();
    wait.until(titleIs("Submitted Successfully!"));
  }

  private void checkSubmitButton(String buttonId) {
    driver.get(appServer.whereIs("click_tests/html5_submit_buttons.html"));
    String name = "Gromit";

    driver.findElement(By.id("name")).sendKeys(name);
    driver.findElement(By.id(buttonId)).click();

    wait.until(titleIs("Submitted Successfully!"));

    assertThat(driver.getCurrentUrl(), containsString("name="+name));
  }
}
