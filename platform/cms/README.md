## CMS module

Represents content storage and rendering services. <br/>

Content types are:
* **Notification** <br/>
As usual this is email or sms template text to send to client. We use [pebbletemplates](https://pebbletemplates.io/) engine 
for content generation.

* **PDF** <br/>
PDF's are as usual used to store client's agreements or other legal documents. Can be sent as email attachment. 
We use [itext](https://github.com/itext/itextpdf/) engine  for PDF generation.

* **Embeddable** <br/>
Template that could be embedded into **Notification** type. For example, email header/footer.

Go Admin -> CMS to view registered items.__
