import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scapper {

	public static void main(String[] args) throws IOException {

		Scapper theHinduScapper = new Scapper();

		Document mainPage = theHinduScapper.loadURL("https://www.thehindu.com/archive/");

		Element webArchive = mainPage.getElementById("archiveWebContainer");

		Elements archiveBorder  = webArchive.getElementsByClass("archiveBorder");
		Elements webArchiveYearContainer = archiveBorder.first().children();

		for(Iterator<Element> childNodes = webArchiveYearContainer.iterator(); childNodes.hasNext();){
			Element yearNode = childNodes.next();
			if(yearNode.attr("class").toString().equals("archiveH2")){
				Element links = null;
				String currentYear = yearNode.text(); 
				System.out.println(yearNode.text());
				do{
					Element monthList = childNodes.next();
					if(monthList.attr("class").toString().equals("archiveMonthList")){
						links = monthList;
						break;
					}
				}while(childNodes.hasNext());
				Elements anchorTag = links.getElementsByTag("a");

				for(Iterator<Element> hrefLink = anchorTag.iterator(); hrefLink.hasNext();){
					Element aTag = hrefLink.next();
					String monthLink = aTag.attr("href");
					String monthName = aTag.text();

					Document daysArchivePage = theHinduScapper.loadURL(monthLink);

					Elements monthDays = daysArchivePage.getElementsByClass("ui-state-default");

					for(Iterator<Element> calendarIterator = monthDays.iterator(); calendarIterator.hasNext();){
						Element monthDay =  calendarIterator.next();
						String dayLink = monthDay.attr("href").toString();
						String day = monthDay.text();
						Document daysTitlePage = theHinduScapper.loadURL(dayLink);
						Elements archiveList = daysTitlePage.getElementsByClass("archive-list");

						for(Iterator<Element> ulNodes = archiveList.iterator(); ulNodes.hasNext();){
							Element ulTag  = ulNodes.next();
							Elements acticleAnchorTag = ulTag.getElementsByTag("a");

							for(Iterator<Element> anchorTagItr = acticleAnchorTag.iterator();anchorTagItr.hasNext();){
								Element articleLink = anchorTagItr.next();
								String articleTitle = articleLink.text();
								//System.out.println(articleLink.text()+"  "+articleLink.attr("href").toString());
								Document articleFinalPage = theHinduScapper.loadURL(articleLink.attr("href").toString());
								Elements authortag =  articleFinalPage.getElementsByClass("auth-nm lnk");
								String authorName = "anonymous";
								if(authortag != null && authortag.first() != null){
									authorName =  authortag.first().text();
								}
								System.out.println("["+currentYear+"/"+monthName+"/"+day+" ] Title :"+articleTitle+ " Author: "+authorName);
							}

						}
					}

				}
			}
		}
		//System.out.println(webArchiveYear.first().outerHtml());

		//System.out.println(webArchiveYear.first().siblingElements());
	}

	public Document loadURL(String url){
		Document doc = null;
		try{
			doc = Jsoup.connect(url).get();

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return doc;
	}

}
