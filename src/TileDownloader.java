import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * TMS tile downloader thread class. 
 * 
 * @author aecavac
 *
 */
public class TileDownloader implements Runnable {

	public TileDownloader() {
	}

	// Variables
	private String tileService;
	private String destPath;

	private int zoomLevelStartIndex = 0;
	private int zoomLevelEndIndex = 0;

	private int xStartIndex = 0;
	private int yStartIndex = 0;

	private int xEndIndex = 0;
	private int yEndIndex = 0;

	// Global Statistic Parameters
	public static int downImageCount = 0;

	/**
	 * Download tiles.
	 */
	private void downloadTiles() {

		String destImgPath = "";
		String destImgName = "";

		String tileServiceImg = "";

		for (int z = zoomLevelStartIndex; z <= zoomLevelEndIndex; z++) {

			int maxXOfCurrentZoomLevel;
			int maxYOfCurrentZoomLevel;
			int xStart;
			int yStart;
			int xEnd;
			int yEnd;

			maxXOfCurrentZoomLevel = (int) (Math.pow(2, z));
			maxYOfCurrentZoomLevel = (int) (Math.pow(2, z));

			// X start
			if ((z == zoomLevelStartIndex) && (xStartIndex != 0)) {
				xStart = xStartIndex;
			} else {
				xStart = 0;
			}

			// X end
			if ((z == zoomLevelEndIndex) && (xEndIndex != 0)) {
				xEnd = xEndIndex;
			} else {
				xEnd = maxXOfCurrentZoomLevel - 1;
			}

			for (int x = xStart; x <= xEnd; x++) {

				// Y Start
				if ((z == zoomLevelStartIndex) && (x == xStart)
						&& (yStartIndex != 0)) {
					yStart = yStartIndex;
				} else {
					yStart = 0;
				}

				// Y end
				if ((z == zoomLevelEndIndex) && (x == xEnd) && (yEndIndex != 0)) {
					yEnd = yEndIndex;
				} else {
					yEnd = maxYOfCurrentZoomLevel - 1;
				}

				for (int y = yStart; y <= yEnd; y++) {

					destImgPath = getDestPath() + "/" + z + "/" + x;
					destImgName = y + ".png";
					tileServiceImg = getTileService() + "/" + z + "/" + x + "/" + y
							+ ".png";
					
					//Check if image exist.
					File checkFile = new File(destImgPath + "/" + destImgName);
					if(checkFile.exists()){
//						System.out.println("EXIST : " + destImgPath + "/" + destImgName);
						continue;
					}
//					System.out.println("DOWNLOAD : " + destImgPath + "/" + destImgName);

					final byte[] imgBytes;
					
					if(this.getTileService().toUpperCase().startsWith("HTTPS")){
						imgBytes = this.getImageByteArrayHTTPS(tileServiceImg);
					}else{
						imgBytes = this.getImageByteArrayHTTP(tileServiceImg);
					}
					
					if (imgBytes == null) {
						// TODO : SAVE & TERMINATE || WAIT TO GET CONN
						return;
					}

					final boolean writeResult = this.writeImageToDestination(
							destImgPath, destImgName, imgBytes);

					if (!writeResult) {
						// TODO : SAVE & TERMINATE || WAIT TO GET CONN
						return;
					}

					downImageCount++;

					// Clean
					destImgPath = "";
					destImgName = "";
				}

				// Clean
				yStartIndex = 0;
			}

			// Clean
			xStartIndex = 0;
		}
		System.out.println("COMPLATED!");
	}

	/**
	 * Download image from http service.
	 * @param urlStr
	 * @return
	 */
	private byte[] getImageByteArrayHTTP(final String urlStr) {
		try {
			final URL url = new URL(urlStr);
			final InputStream in = new BufferedInputStream(url.openStream());
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			final byte[] response = out.toByteArray();
			return response;
		} catch (final Exception e) {
			System.out.println("getImageByteArrayHTTP:");
			System.out.println(urlStr);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Download image from https service.
	 * @param urlStr
	 * @return
	 */
	private byte[] getImageByteArrayHTTPS(final String urlStr) {
		try {
			URL url = new URL(urlStr);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			if (con == null){
				return null;
			}
			
			final InputStream in = con.getInputStream();
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			
			out.close();
			in.close();
			final byte[] response = out.toByteArray();
			return response;						
		} catch (final Exception e) {
			System.out.println("getImageByteArrayHTTPS:");
			System.out.println(urlStr);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Write image to destination.
	 * 
	 * @param destinationPath
	 * @param imageName
	 * @param image
	 * @return
	 */
	private boolean writeImageToDestination(final String destinationPath,
			final String imageName, final byte[] image) {
		try {
			final File file = new File(destinationPath);
			file.mkdirs();

			final FileOutputStream fos = new FileOutputStream(destinationPath
					+ "/" + imageName);
			fos.write(image);
			fos.close();
			return true;
		} catch (final Exception e) {
			System.out.println("writeImageToDestination:");
			System.out.println(destinationPath);
			e.printStackTrace();
			return false;
		}
	}
	
	public String getTileService() {
		return tileService;
	}

	public void setTileService(String tileService) {
		this.tileService = tileService;
	}

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}		

	public int getZoomLevelStartIndex() {
		return zoomLevelStartIndex;
	}

	public void setZoomLevelStartIndex(int zoomLevelStartIndex) {
		this.zoomLevelStartIndex = zoomLevelStartIndex;
	}

	public int getZoomLevelEndIndex() {
		return zoomLevelEndIndex;
	}

	public void setZoomLevelEndIndex(int zoomLevelEndIndex) {
		this.zoomLevelEndIndex = zoomLevelEndIndex;
	}

	public int getxStartIndex() {
		return xStartIndex;
	}

	public void setxStartIndex(int xStartIndex) {
		this.xStartIndex = xStartIndex;
	}

	public int getyStartIndex() {
		return yStartIndex;
	}

	public void setyStartIndex(int yStartIndex) {
		this.yStartIndex = yStartIndex;
	}

	public int getxEndIndex() {
		return xEndIndex;
	}

	public void setxEndIndex(int xEndIndex) {
		this.xEndIndex = xEndIndex;
	}

	public int getyEndIndex() {
		return yEndIndex;
	}

	public void setyEndIndex(int yEndIndex) {
		this.yEndIndex = yEndIndex;
	}
	
	@Override
	public void run() {
		this.downloadTiles();
	}
}