## Marketing core module

Marketing email is structure of 
* marketing image
* marketing body
* footer with unsubscribe url
   
Main definitions
* **Marketing Model** - object (**fintech.marketing.MarketingModel**) that can be used in every email/sms body or template.

* **Marketing Template** - contains html template being sent to client (mandatory). Also contains emailBody and imageFile (used only for template preview purposes)<br/>
**emailBody** available through MarketingModel.content variable.<br/>
**imageFile** available through MarketingModel.mainImageBase64String variable.

* **Marketing Campaign** - setup of Marketing Template. Used to override marketing emailBody and imageFile (this data will be sent to client) <br/>
Also, promocode object could be set (MarketingModel.promoCode variable). <br/>
Audience selection is required too (predicates for clients selection). <br/>
Campaign consists of two setups - main and reminder. <br/> 
Main setup sends immediately according set triggering time.<br/>
Remind setup sends after set X hours if client had not applied for a new loan application.<br/>
Marketing Campaign can be simple and automatic. Automatic could be sent automatically every day/week/month. 

* **Marketing Communication** - result of Marketing Campaign execution. Holds unique clicks/views statistic calculated by HyperLogLog algorithm.



  

