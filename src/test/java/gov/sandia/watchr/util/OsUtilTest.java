package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

public class OsUtilTest {
	
	@Test
	public void testConvertToOsFileSeparators() {
		String unixPath = "/home/bob/data/file";
		String winPath = "C:\\Users\\Bob\\Data\\file";
		
		String unixPathConverted = OsUtil.convertToOsFileSeparators(unixPath);
		String winPathConverted = OsUtil.convertToOsFileSeparators(winPath);
		
		// The following is not a conventional assert section of a
		// unit test, since one platform running unit tests will only hit
		// one of these blocks.  However, we can't static-mock
		// OsUtil.isWindows() since that's the class being tested,
		// and tests are regularly run on both Windows and Unix
		// platforms anyway, so in practice, we will assert for
		// both code paths.
		if(SystemUtils.IS_OS_WINDOWS) {
			assertEquals(unixPathConverted, "\\home\\bob\\data\\file");
			assertEquals(winPathConverted, "C:\\Users\\Bob\\Data\\file");
		} else {
			assertEquals(unixPathConverted, "/home/bob/data/file");
			assertEquals(winPathConverted, "C:/Users/Bob/Data/file");
		}
	}
}

