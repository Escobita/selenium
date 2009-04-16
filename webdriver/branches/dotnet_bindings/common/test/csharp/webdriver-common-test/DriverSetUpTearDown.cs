using NUnit.Framework;

namespace OpenQa.Selenium
{
    [SetUpFixture]
    public class DriverSetUpTearDown
    {

        [SetUp]
        public void SetUp()
        {
            Environment.Instance.CreateFreshDriver();
        }

        [TearDown]
        public void TearDown()
        {
            Environment.Instance.GetDriver().Close();
        }

    }
}
