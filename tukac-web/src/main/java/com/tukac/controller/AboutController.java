package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.model.AppSetting;
import com.tukac.repository.AppSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/about")
public class AboutController {

    private static final String SETTING_KEY = "about_page";

    @Autowired
    private AppSettingRepository settingRepository;

    private static final String DEFAULT_ABOUT = """
{
  "history": "The TUK Ability Club (TUKAC) was founded in 2025 at the Technical University of Kenya with the mission of creating an inclusive environment for students with disabilities. The club was established by a group of passionate students and staff who believed every student deserves equal opportunity in education and campus life.\\n\\nTUKAC champions disability awareness, advocates for accessibility improvements on campus, and runs mentorship and empowerment programs for students with various disabilities.",
  "constitution": "Constitution of the Technical University of Kenya Ability Club (TUKAC) 2025/2026\\n\\nPREAMBLE\\nWe, the students of the Technical University of Kenya, recognizing the need for a unified voice, a supportive community, and dedicated advocacy for students with disabilities, hereby establish this constitution to govern the Technical University of Kenya Ability Club (TUKAC). We are committed to the principles of disability justice, inclusion, peer support, and the relentless pursuit of an accessible and equitable university experience for all.\\n\\nARTICLE I: NAME OF ORGANIZATION\\nThe official name of this organization shall be the Technical University of Kenya Ability Club, hereafter referred to as TUKAC.\\n\\nARTICLE II: MISSION & PURPOSE\\nThe mission of TUKAC is to empower students with disabilities at the Technical University of Kenya through community, advocacy, and education. Our specific purposes are:\\n1. To create a safe, supportive, and social community for students with disabilities and chronic illnesses.\\n2. To advocate for the rights, needs, and accessibility for disabled students at the institutional, local, and national levels.\\n3. To educate the campus community about disability justice, etiquette, accessibility, and inclusion.\\n4. To provide peer support and share resources for navigating academic, social, and administrative systems.\\n5. Establish partnerships with disability-supporting institutions.\\n6. Help enrolled students with disabilities with relevant help and information on how to register with the National Council for Persons With Disabilities (NCPWD).\\n7. Organize academic, social and empowerment activities.\\n\\nARTICLE III: MEMBERSHIP\\nSection 1: Eligibility\\nMembership is open to all currently enrolled students at the Technical University of Kenya. No student shall be denied membership on the basis of actual or perceived disability, race, gender, religion, sexual orientation, nationality, or any other identity.\\n\\nSection 2: Types of Membership\\nA. Voting Members: Students with disabilities or chronic illnesses. Voting members have the right to vote in all elections, on constitutional amendments, and on official club stances.\\nB. Allied Members: Non-disabled students who support the Club's mission. Allied members may participate in all activities but shall not vote on matters of advocacy direction or constitutional change.\\n\\nSection 3: Expectations & Dues\\n1. All members are expected to respect the Club's mission and community guidelines. Membership is free to ensure accessibility.\\n2. Voluntary contributions may be requested for specific events or projects.\\n3. No individual shall be forced to join the club.\\n4. A member can leave the club at their will and will not be retained as a member against their wish; they will have no obligations to the club and the club will have no obligation to them.\\n\\nARTICLE IV: OFFICERS & LEADERSHIP STRUCTURE\\nSection 1: Elected Officers\\nThe Executive Committee of TUKAC shall consist of the following elected officers:\\na. Chairperson: Oversees Club operations, presides over meetings, and serves as the primary representative of TUKAC.\\nb. Vice Chairperson: Assists the Chairperson, assumes duties in their absence, and focuses on internal member support and welfare.\\nc. Secretary: Maintains all Club records, takes and disseminates accessible meeting minutes, and manages official communications.\\nd. Treasurer: Manages the Club's finances, prepares budgets, and handles funding requests in accordance with university policies.\\ne. Accessibility Officer: Ensures all Club meetings, events, and materials are accessible. Responsibilities include arranging accommodations (e.g., sign language interpretation, venue accessibility audits), and acting as the primary contact for member access needs. This role must be held by a student with a disability.\\nf. Advocacy Chair: Organizes advocacy campaigns, liaises with university administration and the Disability Support Service, and represents the Club's advocacy interests.\\n\\nSection 2: Advisor (Patron/Matron)\\nThe Club shall have a Faculty/Staff Advisor, preferably from the Dean of Students Office, who from time to time shall be present in the club meetings, to provide guidance and institutional continuity.\\n\\nARTICLE V: ELECTIONS & REMOVAL\\nSection 1: Election Process\\nElections for all Executive Committee positions shall be held annually, in concurrence with the Student Association of the Technical University of Kenya (SATUK) elections. Nominations shall be open for a period of one month prior to the election date. Voting shall be conducted openly and by secret ballot, as per the electoral guidelines of the Technical University of Kenya.\\n\\nSection 2: Term of Office\\nOfficers shall serve a term of one (1) academic year. Officers may stand for re-election for one consecutive term.\\n\\nSection 3: Removal from Office\\na) Any officer may be removed from office for gross misconduct, negligence of duties, or violation of Club principles.\\nb) Removal requires a two-thirds (2/3) majority vote of the voting membership present at a specially convened meeting, provided notice of the removal vote is given one week in advance.\\n\\nARTICLE VI: MEETINGS & PROCEDURES\\nSection 1: Frequency\\nGeneral meetings of the Club shall be held at least once per month during the academic semester.\\n\\nSection 2: Accessibility Protocol\\na) All meetings and events shall be held in accessible locations.\\nb) Notice of meetings shall be publicized at least one week in advance, including a clear method for members to request specific accommodations, with the exception of impromptu meetings.\\nc) The Accessibility Officer is responsible for coordinating and ensuring all accommodations are provided.\\n\\nSection 3: Decision-Making & Quorum\\na) A quorum for conducting official business shall be one-third (1/3) of the registered voting membership.\\nb) General motions shall pass by a simple majority (50% + 1) of voting members present. Major decisions, including constitutional amendments and official advocacy stances, require a two-thirds (2/3) majority.\\n\\nSection 4: Attendance\\nMembers are expected to attend meetings regularly. Failure to attend three (3) consecutive general meetings without prior communication or a valid reason may result in a penalty, as determined by the Executive Committee, to a maximum of Kenya Shillings 200, to be paid into the Club fund.\\n\\nARTICLE VII: AMENDMENTS\\nSection 1: Lock-in Period\\nThis Constitution shall not be amended until after it has been in effect for three (3) officially convened general meetings of the Club.\\n\\nSection 2: Amendment Process\\nThis Constitution may be amended by a two-thirds (2/3) majority vote of the voting membership present at a meeting, provided that the proposed amendment has been distributed in writing to the entire membership at least one (1) week prior to the vote.\\n\\nARTICLE VIII: NON-DISCRIMINATION & CODE OF CONDUCT\\nSection 1: Non-Discrimination Statement\\nTUKAC does not discriminate on the basis of disability, race, gender, sexual orientation, age, religion, nationality, or any other identity. We are committed to creating an anti-ableist, anti-racist, and harassment-free environment.\\n\\nSection 2: Conflict Resolution\\nGrievances between members or against officers shall first be addressed privately with the involved parties. If unresolved, the matter may be brought to the Executive Committee, excluding any involved officer, for mediation. The Committee's decision may be appealed to the full voting membership at a general meeting.\\n\\nARTICLE IX: FINANCE\\n1. Club funds may originate from contributions, donations, grants, fundraising, and institutional support.\\n2. The Treasurer shall maintain transparent accounts and produce annual reports.\\n3. Expenditure approval must be jointly signed by: a) Treasurer, b) Chairperson, c) Vice Chairperson, d) Secretary. In the instance when the Chairperson has medical issues or has permitted, then one of the other executives will sign with the Chairperson's permission.\\n\\nARTICLE X: RATIFICATION\\nThis Constitution shall be considered ratified and immediately effective upon receiving a two-thirds (2/3) majority vote of the founding members present at the inaugural meeting.",
  "previousChairpersons": [],
  "contact": {
    "email": "tukac@tukenya.ac.ke",
    "chairperson": {"name": "Current Chairperson", "phone": "+254 700 000 001"},
    "viceChairperson": {"name": "Current Vice Chairperson", "phone": "+254 700 000 002"},
    "secretary": {"name": "Current Secretary", "phone": ""},
    "treasurer": {"name": "Current Treasurer", "phone": ""},
    "location": "Technical University of Kenya, Haile Selassie Avenue, Nairobi"
  }
}
""";

    @GetMapping
    public ResponseEntity<ApiResponse<String>> getAbout() {
        String value = settingRepository.findById(SETTING_KEY)
                .map(AppSetting::getValue)
                .orElse(DEFAULT_ABOUT);
        return ResponseEntity.ok(ApiResponse.ok(value));
    }

    @PutMapping
    @PreAuthorize("hasRole('CHAIRPERSON')")
    public ResponseEntity<ApiResponse<String>> updateAbout(@RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.isBlank())
            return ResponseEntity.badRequest().body(ApiResponse.error("Content is required"));

        AppSetting setting = new AppSetting(SETTING_KEY, content);
        settingRepository.save(setting);
        return ResponseEntity.ok(ApiResponse.ok("About page updated", content));
    }
}
