package fintech.presence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * <b>Outbound record possible statuses:</b><br/>
 * <table>
 * <tr><td><0</td><td> The record is currently being handled by an agent</td></tr>
 * <tr><td>1</td><td>Initial record</td></tr>
 * <tr><td>2</td><td>Scheduled record</td></tr>
 * <tr><td>3</td><td>Invalid record type of record</td></tr>
 * <tr><td>22</td><td>Scheduled record that has reached the maximum number of calls per day</td></tr>
 * <tr><td>23</td><td>Invalid record type of record that has reached the maximum number of calls per day</td></tr>
 * <tr><td>41</td><td>Initial record. Same as status 1, but in this case the load that the record belongs to is disabled</td></tr>
 * <tr><td>42</td><td>Scheduled record. Same as status 2, but in this case the load that the record belongs to is disabled</td></tr>
 * <tr><td>43</td><td>Invalid record type of record. Same as status 3, but in this case the load that the record belongs to is disabled</td></tr>
 * <tr><td>62</td><td>Scheduled record that has reached the maximum number of calls per day. Same as status 22, but in this case the load that the record belongs to is disabled</td></tr>
 * <tr><td>63</td><td>Invalid record type of record that has reached the maximum number of calls per day. Same as status 23, but in this case the load that the record belongs to is disabled</td></tr>
 * <tr><td>91</td><td>Record whose alternative phone numbers have a status other than 0 or 53 (Valid phone number or Wrong phone number, respectively) and, at least, one of the phone numbers has a status of 2 or 52 (Phone number does not exist)</td></tr>
 * <tr><td>92</td><td>Record whose alternative phone numbers have a status other than 0 (Valid phone number) and, at least, one of the phone numbers has a status of 53 (Wrong phone number)</td></tr>
 * <tr><td>93</td><td>Record moved to another service</td></tr>
 * <tr><td>94</td><td>Unloaded record</td></tr>
 * <tr><td>95</td><td>Record whose alternative phone numbers have a status of 1 or 51 (Do-Not-Call phone number)</td></tr>
 * <tr><td>96</td><td>Incomplete record</td></tr>
 * <tr><td>98</td><td>Record that has reached the maximum number of calls for some of its counters</td></tr>
 * <tr><td>99</td><td>Record completed. Please, refer to the LASTQCODE field to check for its qualification code</td></tr>
 * <tr><td>>1000</td><td>Locked record. Subtracting 1000 from the record status value provides one of the above defined values, as well as information about the record status before this was changed to locked status</td></tr>
 * </table>
 * </p>
 * <p>
 * <b>Alternative phone status code:</b>
 * <table>
 * <tr><td>0</td><td>Valid phone number</td></tr>
 * <tr><td>1</td><td>Do-Not-Call phone number (System)</td></tr>
 * <tr><td>2</td><td>Phone number does not exist (System)</td></tr>
 * <tr><td>51</td><td> Do-Not-Call phone number (Agent)</td></tr>
 * <tr><td>52</td><td> Phone number does not exist (Agent)</td></tr>
 * <tr><td>53</td><td> Wrong phone number (Agent)</td></tr>
 * </table>
 * </p>
 */
public enum OutboundLoadRecordStatus {

    ASSIGNED,
    PENDING,
    SCHEDULED,
    INVALID,
    UNLOADED,
    INCOMPLETE,
    MAX_NUM_CALLS_REACHED,
    DO_NOT_CALL,
    COMPLETED;

    private static Map<Integer, OutboundLoadRecordStatus> namesMap = new HashMap<>();

    static {
        namesMap.put(1, PENDING);
        namesMap.put(2, SCHEDULED);
        namesMap.put(3, INVALID);
        namesMap.put(22, MAX_NUM_CALLS_REACHED);
        namesMap.put(23, MAX_NUM_CALLS_REACHED);
        namesMap.put(91, INVALID);
        namesMap.put(92, PENDING);
        namesMap.put(93, UNLOADED);
        namesMap.put(94, UNLOADED);
        namesMap.put(95, DO_NOT_CALL);
        namesMap.put(96, INCOMPLETE);
        namesMap.put(98, MAX_NUM_CALLS_REACHED);
        namesMap.put(99, COMPLETED);
    }

    @JsonCreator
    public static OutboundLoadRecordStatus forValue(Integer value) {
        if (value >= 41 && value <= 63) {
            return namesMap.get(value - 40);
        }
        if (value > 1000) {
            return namesMap.get(value - 1000);
        }
        if (value < 0) {
            return ASSIGNED;
        }
        return namesMap.get(value);
    }

    @JsonValue
    public Integer toValue() {
        for (Map.Entry<Integer, OutboundLoadRecordStatus> entry : namesMap.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }

        return null;
    }
}
