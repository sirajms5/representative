package disk;

import java.util.List;

import classes.HOCMember;
import db.RepresentativeCRUD;
import utilities.LogKeeper;

public class JsonWriter {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public void writeHocMembersJson() {
        RepresentativeCRUD representativeCRUD = new RepresentativeCRUD();
        List<HOCMember> hocMembers = representativeCRUD.getHocMembers();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int index = 0; index < hocMembers.size(); index++) {
            HOCMember member = hocMembers.get(index);
            jsonBuilder.append(member.toJson());
            if (index < hocMembers.size() - 1) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("]");
        DiskUtilities diskUtilities = new DiskUtilities();
        diskUtilities.txtWriter(jsonBuilder.toString(), "C:\\xampp\\htdocs\\representative\\private\\java\\disk\\files\\json\\HOCMembers.json");
        logKeeper.appendLog("Created JSON file for HOC members");
    }    
}
