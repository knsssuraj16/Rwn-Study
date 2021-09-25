package com.rwn.rwnstudy.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.rwn.rwnstudy.R;

public class PrivacyAndPoliciesActivity extends AppCompatActivity {

    TextView textViewPri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_and_policies);
        textViewPri = findViewById(R.id.textview_privacyAnd_polices);
        textViewPri.setText("This agreement was written in English (US). To the extent any translated version of this agreement conflicts with the English version, the English version controls.  Written date January 25 2019 Statement of Rights and Responsibilities. This Statement of Rights and Responsibilities (\"Statement,\" \"Terms,\" or \"SRR\") derives from the RWN Principles, and is our terms of service that governs our relationship with users and others who interact with RWN, as well as RWN brands, products and services, which we call the“RWN Services” or “Services”. By using or accessing the RWN Services, you agree to this Statement, as updated from time to time in accordance with Section 13 below. Additionally, you will find resources at the end of this document that help you understand how RWN works. Because RWN provides a wide range of Services, we may ask you to review and accept supplemental terms that apply to your interaction with a specific app, product, or service. To the extent those supplemental terms conflict with this SRR, the supplemental terms associated with the app, product, or service govern with respect to your use of such app, product or service to the extent of the conflict.\n" +
                "1. Privacy: Your privacy is very important to us. We designed our Data Policy to make important disclosures about how you can use RWN to share with others and how we collect and can use your content and information. We encourage you to read the Data Policy, and to use it to help you make informed decisions.\n" +
                "2. Sharing Your Content and Information: You own all of the content and information you post on RWN, and you can control how it is shared through your privacy and application settings. In addition:\n" +
                "1.  For content that is covered by intellectual property rights, like photos and videos (IP content), you specifically give us the following permission, subject to your privacy and application settings: you grant us a non-exclusive, transferable, sub-licensable, royalty-free, worldwide license to use any IP content that you post on or in connection with RWN (IP License). This IP License ends when you delete your IP content or your account unless your content has been shared with others, and they have not deleted it.\n" +
                "2.  When you delete IP content, it is deleted in a manner similar to emptying the recycle bin on a computer. However, you understand that removed content may persist in backup copies for a reasonable period of time (but will not be available to others).\n" +
                "3. When you use an application, the application may ask for your permission to access your content and information as well as content and information that others have shared with you.  We require applications to respect your privacy, and your agreement with that application will control how the application can use, store, and transfer that content and information.  (To learn more about Platform, including how you can control what information other people may share with applications, read our Data Policy and Platform Page.)\n" +
                "4. When you publish content or information using RWN App, it means that you are allowing everyone, including people off of RWN, to access and use that information, and to associate it with you (i.e., your name and profile picture).\n" +
                "5. We always appreciate your feedback or other suggestions about RWN, but you understand that we may use your feedback or suggestions without any obligation to compensate you for them (just as you have no obligation to offer them).\n" +
                "3. Safety: We do our best to keep RWN safe, but we cannot guarantee it. We need your help to keep RWN safe, which includes the following commitments by you:\n" +
                "1. You will not post unauthorized commercial communications (such as spam) on RWN.\n" +
                "2. You will not collect users' content or information, or otherwise access RWN, using automated means (such as harvesting bots, robots, spiders, or scrapers) without our prior permission.\n" +
                "3. You will not engage in unlawful multilevel marketing, such as a pyramid scheme, on RWN.\n" +
                "4. You will not upload viruses or other malicious code.\n" +
                "5. You will not solicit login information or access an account belonging to someone else.\n" +
                "6. You will not bully, intimidate, or harass any user.\n" +
                "7. You will not post content that: is hate speech, threatening, or pornographic; incites violence; or contains nudity or graphic or gratuitous violence.\n" +
                "8. You will not develop or operate a third-party application containing alcohol-related, dating or other mature content (including advertisements) without appropriate age-based restrictions.\n" +
                "9.  You will not use RWN to do anything unlawful, misleading, malicious, or discriminatory.\n" +
                "10. You will not do anything that could disable, overburden, or impair the proper working or other RWN functionality.\n" +
                "11.  You will not facilitate or encourage any violations of this Statement or our policies.\n" +
                "information, and we need your help to keep it that way. Here are some commitments you make to us relating to registering and maintaining the security of your account:\n" +
                "1. You will not provide any false personal information on RWN, or create an account for anyone other than yourself without permission.\n" +
                "2.  You will not create more than one personal account.\n" +
                "3.  If we disable your account, you will not create another one without our permission.\n" +
                "4.  You will not use your personal time line primarily for your own commercial gain, and will use a RWN Page for such purposes.\n" +
                "8. You will not share your password (or in the case of developers, your secret key), let anyone else access your account, or do anything else that might jeopardize the security of your account.\n" +
                "9. You will not transfer your account (including any Page or application you administer) to anyone without first getting our written permission.\n" +
                "5. Protecting Other People's Rights We respect other people's rights, and expect you to do the same.\n" +
                "1. You will not post content or take any action on RWN that infringes or violates someone else's rights or otherwise violates the law.\n" +
                "2. We can remove any content or information you post on RWN if we believe that it violates this Statement or our policies.\n" +
                "4. If we remove your content for infringing someone else's copyright, and you believe we removed it by mistake, we will provide you with an opportunity to appeal.\n" +
                "5. If you repeatedly infringe other people's intellectual property rights, we will disable your account when appropriate.\n" +
                "6. You will not use our copyrights or Trademarks or any confusingly similar marks, except as expressly permitted by our Brand Usage Guidelines or with our prior written permission.\n" +
                "7. If you collect information from users, you will: obtain their consent, make it clear you (and not RWN) are the one collecting their information, and post a privacy policy explaining what information you collect and how you will use it.8.You will not post anyone's identification documents or sensitive financial information on RWN.\n" +
                "6.  Mobile and Other Devices \n" +
                "1. We currently provide our mobile services for free, but please be aware that your carrier's normal rates and fees, such as text messaging and data charges, will still apply.\n" +
                "2. You provide consent and all rights necessary to enable users to sync (including through an application) their devices with any information that is visible to them on RWN.\n" +
                "7.  Payments: If you make a payment on RWN, you agree to our Payments Terms unless it is stated that other terms apply.\n" +
                "8.Special Provisions Applicable to Developers/Operators of Applications and Websites If you are a developer or operator of a Platform application or website or if you use Social Plugins, you must comply with the RWN Platform Policy.\n" +
                "9.About Advertisements and Other Commercial Content Served or Enhanced by RWN. Our goal is to deliver advertising and other commercial or sponsored content that is valuable to our users and advertisers. In order to help us do that, you agree to the following:\n" +
                "1. You give us permission to use your name, profile picture, content, and information in connection with commercial, sponsored, or related content (such as a brand you like) served or enhanced by us. This means, for example, that you permit a business or other entity to pay us to display your name and/or profile picture with your content or information, without any compensation to you. If you have selected a specific audience for your content or information, we will respect your choice when we use it.\n" +
                "2. We do not give your content or information to advertisers without your consent.\n" +
                "10.  Special Provisions Applicable to Advertisers: If you use our self-service advertising creation interfaces for creation, submission and/or delivery of any advertising or other commercial or sponsored activity or content (collectively, the “Self-Serve Ad Interfaces”), you agree to our Self-Serve Ad Terms. In addition, your advertising or other commercial or sponsored activity or content placed on RWN or our publisher network will comply with our Advertising Policies.\n" +
                "11.  Special Provisions Applicable to Pages: If you create or administer a Page on RWN, or run a promotion or an offer from your Page, you agree to our Pages Terms.\n" +
                "12.  Special Provisions Applicable to Software:\n" +
                "1. If you download or use our software, such as a stand-alone software product, an app, or a browser plugin, you agree that from time to time, the software may download and install upgrades, updates and additional features from us in order to improve, enhance, and further develop the software.\n" +
                "2. You will not modify, create derivative works of, decompile, or otherwise attempt to extract source code from us, unless you are expressly permitted to do so under an open source license, or we give you express written permission.\n" +
                "13.   Amendments:\n" +
                "1. We’ll notify you before we make changes to these terms and give you the opportunity to review and comment on the revised terms before continuing to use our Services.\n" +
                "2. If we make changes to policies, guidelines or other terms referenced in or incorporated by this Statement, we may provide notice on the Site Governance Page.\n" +
                "3. Your continued use of the RWN Services, following notice of the changes to our terms, policies or guidelines, constitutes your acceptance of our amended terms, policies or guidelines.\n" +
                "14.   Termination: If you violate the letter or spirit of this Statement, or otherwise create risk or possible legal exposure for us, we can stop providing all or part of RWN to you. We will notify you by email or at the next time you attempt to access your account. You may also delete your account or disable your application at any time. In all such cases, this Statement shall terminate, but the following provisions will still apply: 2.2, 2.4, 3-5, 9.3, and 14-18.15. Disputes\n" +
                "1. You will resolve any claim, cause of action or dispute (claim) you have with us arising out of or relating to this Statement or RWN exclusively in India. \n" +
                "2. If anyone brings a claim against us related to your actions, content or information on RWN, you will indemnify and hold us harmless from and against all damages, losses, and expenses of any kind (including reasonable legal fees and costs) related to such claim. Although we provide rules for user conduct, we do not control or direct users' actions on RWN and are not responsible for the content or information users transmit or share on RWN. We are not responsible for any offensive, inappropriate, obscene, unlawful or otherwise objectionable content or information you may encounter on RWN. We are not responsible for the conduct, whether online or offline, of any user of RWN.\n" +
                "3. WE TRY TO KEEP RWN, BUG-FREE, AND SAFE, BUT YOU USE IT AT YOUR OWN RISK. WE ARE PROVIDING RWN AS IS WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES INCLUDING, BUT NOT LIMITED TO, IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. WE DO NOT GUARANTEE THAT RWN WILL ALWAYS BE SAFE, SECURE OR ERROR-FREE OR THAT RWN WILL ALWAYS FUNCTION WITHOUT DISRUPTIONS, DELAYS OR IMPERFECTIONS. RWN IS NOT RESPONSIBLE FOR THE ACTIONS, CONTENT, INFORMATION, OR DATA OF THIRD PARTIES, AND YOU RELEASE US, OUR DIRECTORS, OFFICERS, EMPLOYEES, AND AGENTS FROM ANY CLAIMS AND DAMAGES, KNOWN AND UNKNOWN, ARISING OUT OF OR IN ANY WAY CONNECTED WITH ANY CLAIM YOU HAVE AGAINST ANY SUCH THIRD PARTIES APPLICABLE LAW MAY NOT ALLOW THE LIMITATION OR EXCLUSION OF LIABILITY OR INCIDENTAL OR CONSEQUENTIAL DAMAGES, SO THE ABOVE LIMITATION OR EXCLUSION MAY NOT APPLY TO YOU. IN SUCH CASES, FULNNSWAP'S LIABILITY WILL BE LIMITED TO THE FULLEST EXTENT PERMITTED BY APPLICABLE LAW.\n" +
                "16. Special Provisions Applicable to Users Outside the India. We strive to create a global community with consistent standards for everyone, but we also strive to respect local laws. The following provisions apply to users and non-users who interact with RWN outside the India\n" +
                "1. You consent to having your personal data transferred to and processed in the .india\n" +
                "2. If you are located in a country embargoed by the India, or are on the Indian Treasury Department's list of Specially Designated Nationals you will not engage in commercial activities on RWN (such as advertising or payments) or operate a Platform application or website. You will not use RWN if you are prohibited from receiving products, services, or software originating from the India \n" +
                "3. By \"information\" we mean facts and other information about you, including actions taken by users and non-users who interact with RWN.\n" +
                "4. By \"content\" we mean anything you or other users post, provide or share using RWN Services.\n" +
                "5. By \"data\" or \"user data\" or \"user's data\" we mean any data, including a user's contactor information that you or third parties can retrieve from RWN or provide to RWN through Platform.\n" +
                "6. By \"post\" we mean post on RWN or otherwise make available by using RWN.\n" +
                "7. By \"use\" we mean use, run, copy, publicly perform or display, distribute, modify, translate, and create derivative works of.\n" +
                "8. By \"application\" we mean any application or website that uses or accesses Platform, as well as anything else that receives or has received data from us.  If you no longer access Platform but have not deleted all data from us, the term application will apply until you delete the data.\n" +
                "9. By “Trademarks” we mean the list of trademarks provided here.\n" +
                "18. Other\n" +
                "1. If you are a resident of or have your principal place of business in the India, this Statement is an agreement between you and RWN, Inc.  References to “us,” “we,” and “our” mean either RWN, Inc. or RWN team Limited, as appropriate.\n" +
                "2. This Statement makes up the entire agreement between the parties regarding RWN, and supersedes any prior agreements.\n" +
                "3. If any portion of this Statement is found to be unenforceable, the remaining portion will remain in full force and effect.\n" +
                "4. If we fail to enforce any of this Statement, it will not be considered a waiver.\n" +
                "5.  Any amendment to or waiver of this Statement must be made in writing and signed by us.\n" +
                "6. You will not transfer any of your rights or obligations under this Statement to anyone else without our consent.\n" +
                "7. All of our rights and obligations under this Statement are freely assignable by us in connection with a merger, acquisition, or sale of assets, or by operation of law or otherwise.\n" +
                "8. Nothing in this Statement shall prevent us from complying with the law.\n" +
                "9. This Statement does not confer any third party beneficiary rights.\n" +
                "10. We reserve all rights not expressly granted to you.\n" +
                "11. You will comply with all applicable laws when using or accessing RWN .By using or accessing RWN Services,you agree that we can collect and use such content and information in accordance with the Data Policy as amended from time to time. You may also want to review the following documents, which provide additional information about your use of RWN:*.Payment Terms: These additional terms apply to all payments made on or through RWN nless it is stated that other terms apply.*.Platform Page: This page helps you better understand what happens when you add a third-party application or use RWN Connect, including how they may access and use your data.*. RWN Platform Policies: These guidelines outline the policies that apply to applications, including Connect sites.*.Advertising Policies: These guidelines outline the policies that apply to advertisements placed on RWN.*.Self-Serve Ad Terms: These terms apply when you use the Self-Serve Ad Interfaces to create, submit, or deliver any advertising or other commercial or sponsored activity or content.*.Promotions Guidelines: These guidelines outline the policies that apply if you offer contests, sweepstakes, and other types of promotions on RWN *. RWN Brand Resources: These guidelines outline the policies that apply to use of RWN trademarks, logos and screen shots.*.How to Report Claims of Intellectual Property Infringement*.Pages Terms: These guidelines apply to your use of RWN Pages.*.Community Standards: These guidelines outline our expectations regarding the content you post to RWN and your activity on RWN. To access the Statement of Rights and Responsibilities in several different languages, change the language setting for your RWN session by clicking on the language link in the left corner of most pages.  If the Statement is not available in the language you select, we will default to the English version.\n");
    }
}