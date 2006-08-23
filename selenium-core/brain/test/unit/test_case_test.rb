require File.dirname(__FILE__) + '/../test_helper'

class TestCaseTest < Test::Unit::TestCase
  fixtures :test_cases
  
  def test_should_parse_passed_test_table
    test_table = <<EOF
<DIV>
<TABLE cellSpacing=1 cellPadding=1 border=1>
<TBODY>
<TR bgColor=#ccffcc>
<TD colSpan=3>Test Open<BR></TD></TR>
<TR style="CURSOR: pointer" bgColor=#eeffee _extended="true">
<TD>open</TD>
<TD>../tests/html/test_open.html</TD>
<TD originalHTML="&amp;nbsp;">&nbsp;</TD></TR>
<TR style="CURSOR: pointer" bgColor=#ccffcc _extended="true">
</TBODY></TABLE></DIV>
EOF

    test_case = TestCase.parse(test_table)
    assert_equal("passed", test_case.status)
  end

  def test_should_parse_failed_test_table
    test_table = <<EOF
<DIV>
<TABLE cellSpacing=1 cellPadding=1 border=1>
<TBODY>
<TR bgColor=#ffcccc>
<TD colSpan=3>Test Open<BR></TD></TR>
<TR style="CURSOR: pointer" bgColor=#eeffee _extended="true">
<TD>open</TD>
<TD>../tests/html/test_open.html</TD>
<TD originalHTML="&amp;nbsp;">&nbsp;</TD></TR>
<TR style="CURSOR: pointer" bgColor=#ccffcc _extended="true">
</TBODY></TABLE></DIV>
EOF

    test_case = TestCase.parse(test_table)
    assert_equal("failed", test_case.status)
  end
  
  def test_should_able_to_create_test_case_with_table_and_name
    test_table = "<html>TEST_TABLE</html>"
    name = "TEST_CASE"
    test_case = TestCase.create_by_name_and_table(name, test_table)
    assert_equal(name, test_case.name)
  end
  
  def test_should_create_test_case_with_whole_test_table
    test_table = "<html>TEST_TABLE</html>"
    name = "TEST_CASE"
    test_case = TestCase.create_by_name_and_table(name, test_table)
    assert_equal(test_table, test_case.note)    
  end

end
