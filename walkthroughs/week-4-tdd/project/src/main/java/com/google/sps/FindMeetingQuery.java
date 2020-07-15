// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static com.google.sps.TimeRange.ORDER_BY_START;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //    throw new UnsupportedOperationException("TODO: Implement this method.");
    /*
      Approach:
      1) Find a union of all meetings of all attendees
      2) Convert it into non-overlapping intervals by sorting it
      3) Iterate on sorted array of non-overlapping intervals to find gaps large enough for meeting
    * */

    // Step 1: Find Union of all meetings
    final Collection<String> attendees = request.getAttendees();
    ArrayList<TimeRange> busyRanges = new ArrayList<>();

    for (Event currentEvent : events) {
      boolean attendeePresentInMeeting = false;
      final Set<String> eventAttendees = currentEvent.getAttendees();

      // Iterate over all attendees and see if someone is involved in the meeting
      for (String attendee : attendees) {
        // If you find someone present in the event, you can break
        if (eventAttendees.contains(attendee)) {
          attendeePresentInMeeting = true;
          break;
        }
      }

      // If no attendee is present in this meeting, skip it
      if (!attendeePresentInMeeting) {
        continue;
      }

      // Else we can't conduct a meeting during this time range
      busyRanges.add(currentEvent.getWhen());
    }

    // Step 2: Covert busyRanges into non-overlapping intervals
    // First we need to sort the ranges in ascending order by starting value
    busyRanges.sort(ORDER_BY_START);
    ArrayList<TimeRange> nonOverlappingRanges = new ArrayList<>();

    // Setting sentinel values
    int previousEnd = 0;

    for (TimeRange currentRange : busyRanges) {
      // If this range is contained within an ongoing range, skip it
      if (currentRange.end() <= previousEnd) {
        continue;
      }
      // If this range overlaps with previous range extend the ending
      if (currentRange.start() < previousEnd) {
        previousEnd = currentRange.end();
        continue;
      }
      // Otherwise, previous range ends before current range begins, so add it to the
      // non-overlapping array
      // We can combine combine Step 3 and perform the length check here
      if (currentRange.start() - previousEnd >= request.getDuration()) {
        nonOverlappingRanges.add(TimeRange.fromStartEnd(previousEnd, currentRange.start(), false));
      }
      previousEnd = currentRange.end();
    }

    // check if there is a valid slot at EOD
    if (TimeRange.WHOLE_DAY.end()- previousEnd >= request.getDuration()) {
      nonOverlappingRanges.add(TimeRange.fromStartEnd(previousEnd, TimeRange.WHOLE_DAY.end(), false));
    }

    return nonOverlappingRanges;
  }
}
