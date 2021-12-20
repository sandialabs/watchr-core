package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class FileUtilTest {
    @Test
	public void testCreateRelativeFilePath_Unresolvable() {
		String anchorPath = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2";
		String pathToMakeRelative = "D:" + File.separator + "MyFolder3" + File.separator + "MyFolder4";
		
		// Because the paths share nothing in common, the method should just return the original path
		// the user wanted to make relative.
		String relativePath = FileUtil.createRelativeFilePath(anchorPath, pathToMakeRelative);
		assertEquals(pathToMakeRelative, relativePath);
	}
	
	@Test
	public void testCreateRelativeFilePath_Same() {
		String anchorPath         = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFile.file";
		String pathToMakeRelative = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFile.file";
		
		String relativePath = FileUtil.createRelativeFilePath(anchorPath, pathToMakeRelative);
		assertEquals("", relativePath);
	}
	
	@Test
	public void testCreateRelativeFilePath_SameFolder() {
		String anchorPath         = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFile.file";
		String pathToMakeRelative = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFile2.file";
		String expectedPath = "MyFile2.file";
		
		String relativePath = FileUtil.createRelativeFilePath(anchorPath, pathToMakeRelative);
		assertEquals(expectedPath, relativePath);
	}
	
	@Test
	public void testCreateRelativeFilePath_DeeperOneFolder() {
		String anchorPath         = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFile.file";
		String pathToMakeRelative = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFolder3" + File.separator + "MyFile.file";
		String expectedPath = "MyFolder3" + File.separator + "MyFile.file";
		
		String relativePath = FileUtil.createRelativeFilePath(anchorPath, pathToMakeRelative);
		assertEquals(expectedPath, relativePath);
	}
	
	@Test
	public void testCreateRelativeFilePath_DeeperTwoFolders() {
		String anchorPath         = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFile.file";
		String pathToMakeRelative = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFolder3" + File.separator + "MyFolder4" + File.separator + "MyFile.file";
		String expectedPath = "MyFolder3" + File.separator + "MyFolder4" + File.separator + "MyFile.file";
		
		String relativePath = FileUtil.createRelativeFilePath(anchorPath, pathToMakeRelative);
		assertEquals(expectedPath, relativePath);
	}
	
	@Test
	public void testCreateRelativeFilePath_HigherOneFolder() {
		String anchorPath         = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFile.file";
		String pathToMakeRelative = "C:" + File.separator + "MyFolder" + File.separator + "MyFile.file";
		String expectedPath = ".." + File.separator + "MyFile.file";
		
		String relativePath = FileUtil.createRelativeFilePath(anchorPath, pathToMakeRelative);
		assertEquals(expectedPath, relativePath);
	}
	
	@Test
	public void testCreateRelativeFilePath_HigherTwoFolders() {
		String anchorPath         = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFile.file";
		String pathToMakeRelative = "C:" + File.separator + "MyFile.file";
		String expectedPath = ".." + File.separator + ".." + File.separator + "MyFile.file";
		
		String relativePath = FileUtil.createRelativeFilePath(anchorPath, pathToMakeRelative);
		assertEquals(expectedPath, relativePath);
	}
	
	@Test
	public void testCreateRelativeFilePath_InSiblingFolderWhenAnchorIsFile() {
		String anchorPath         = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2" + File.separator + "MyFile.file";
		String pathToMakeRelative = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder3" + File.separator + "MyFile.file";
		String expectedPath = ".." + File.separator + "MyFolder3" + File.separator + "MyFile.file";
		
		String relativePath = FileUtil.createRelativeFilePath(anchorPath, pathToMakeRelative);
		assertEquals(expectedPath, relativePath);
	}
	
	@Test
	public void testCreateRelativeFilePath_InSiblingFolderWhenAnchorIsFolder() {
		String anchorPath         = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2";
		String pathToMakeRelative = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder3" + File.separator + "MyFile.file";
		String expectedPath = ".." + File.separator + "MyFolder3" + File.separator + "MyFile.file";
		
		String relativePath = FileUtil.createRelativeDirectoryPath(anchorPath, pathToMakeRelative);
		assertEquals(expectedPath, relativePath);
	}
	
	@Test
	public void testCreateRelativeDirectoryPath_ForSiblingFolder() {
		String anchorPath         = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder2";
		String pathToMakeRelative = "C:" + File.separator + "MyFolder" + File.separator + "MyFolder3";
		String expectedPath = ".." + File.separator + "MyFolder3";
		
		String relativePath = FileUtil.createRelativeDirectoryPath(anchorPath, pathToMakeRelative);
		assertEquals(expectedPath, relativePath);
	}
	
	@Test
	public void testCreateAbsolutePath() {
		try {
			File file = new File("resources/testdata/testFile1");
			File parentDir = new File(file.getParent());
			
			String anchorPath = file.toString();
			String pathToMakeAbsolute = "SomeSiblingFile.file";
			
			String actualPath = FileUtil.createAbsoluteFilePath(anchorPath, pathToMakeAbsolute);
			String expectedPath = new File(parentDir, "SomeSiblingFile.file").getAbsolutePath();
			
			assertEquals(expectedPath, actualPath);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
