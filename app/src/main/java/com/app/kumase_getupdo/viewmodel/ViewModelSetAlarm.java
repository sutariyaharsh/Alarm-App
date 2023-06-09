
package com.app.kumase_getupdo.viewmodel;

import static android.content.Context.AUDIO_SERVICE;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.kumase_getupdo.alarm.Activity_AlarmDetails;
import com.app.kumase_getupdo.alarm.Activity_AlarmsList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class ViewModelSetAlarm extends ViewModel {

	/**
	 * A {@link LocalDateTime} object representing the alarm date and time.
	 */
	private MutableLiveData<LocalDateTime> alarmDateTime;

	private final MutableLiveData<Boolean> isAlarmPending = new MutableLiveData<>(false);
	private MutableLiveData<Bundle> pendingAlarmDetails;
	private final MutableLiveData<Boolean> isSettingsActOver = new MutableLiveData<>(false);

	/**
	 * The snooze interval in minutes.
	 */
	private MutableLiveData<Integer> snoozeIntervalInMins;

	/**
	 * The snooze interval in minutes.
	 */
	private MutableLiveData<Integer> snoozeIntervalInSecs;

	/**
	 * The snooze frequency, i.e. the number of times the alarm will be snoozed before it is cancelled automatically.
	 */
	private MutableLiveData<Integer> snoozeFreq;

	/**
	 * Represents the alarm type. Can have only three values:
	 */
	private MutableLiveData<Integer> alarmType;

	/**
	 * The alarm volume.
	 */
	private MutableLiveData<Integer> alarmVolume;

	/**
	 * Indicates whether snooze is ON or OFF.
	 */
	private MutableLiveData<Boolean> isSnoozeOn;

	/**
	 * Represents whether repeat is ON or OFF.
	 */
	private MutableLiveData<Boolean> isRepeatOn;

	/**
	 * This variable indicates whether the date for the alarm is today. It will be {@code true} if the user does not choose a date via the date
	 * picker, or chooses today as the alarm date.
	 */
	private MutableLiveData<Boolean> isChosenDateToday;

	/**
	 * The Uri of the alarm tone. Default value is {@link RingtoneManager#getActualDefaultRingtoneUri(Context, int)} with type {@link
	 * RingtoneManager#TYPE_ALARM}.
	 */
	private MutableLiveData<Uri> alarmToneUri;

	/**
	 * Represents the smallest date that the date picker should support.
	 */
	private MutableLiveData<LocalDate> minDate;

	/**
	 * An integer ArrayList containing the days on which the alarm is to repeat.
	 * <p>
	 * The values follow {@link java.time.DayOfWeek} enum, i.e. Monday is 1 and Sunday is 7.
	 * </p>
	 */
	private MutableLiveData<Integer> repeatDays;

	/**
	 * This variable indicates whether the fragment has been created for a new alarm, or to show the details of an existing alarm. It can have two
	 * values only - {@link Activity_AlarmDetails#MODE_EXISTING_ALARM} or {@link Activity_AlarmDetails#MODE_NEW_ALARM}.
	 */
	private MutableLiveData<Integer> mode;

	/**
	 * The old alarm hour. This variable is useful only when {@link #mode} = {@link Activity_AlarmDetails#MODE_EXISTING_ALARM}. This is passed to
	 * {@link Activity_AlarmsList} along with {@link #oldAlarmMinute} so that the old alarm can be identified and deleted.
	 */
	private MutableLiveData<Integer> oldAlarmHour;

	/**
	 * The old alarm hour. This variable is useful only when {@link #mode} = {@link Activity_AlarmDetails#MODE_EXISTING_ALARM}. This is passed to
	 * {@link Activity_AlarmsList} along with {@link #oldAlarmHour} so that the old alarm can be identified and deleted.
	 */
	private MutableLiveData<Integer> oldAlarmMinute;

	/**
	 * Indicates whether the user has chosen the date explicitly.
	 * <p>
	 * Say the user selects a time that is possible to reach today, but then explicitly choses tomorrow. In that case, the date will not be reverted
	 * to today even if the time is reachable today.
	 * </p>
	 */
	private MutableLiveData<Boolean> hasUserChosenDate;

	/**
	 * The message that will be displayed when the alarm rings. May be {@code null}.
	 */
	private MutableLiveData<String> alarmMessage;
	private MutableLiveData<Integer> alarmId;

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get whether the user has manually chosen a date. Default: {@code false}.
	 *
	 * @return Same as in description.
	 */
	@SuppressWarnings("SimplifiableConditionalExpression")
	public boolean getHasUserChosenDate() {
		if (hasUserChosenDate == null) {
			hasUserChosenDate = new MutableLiveData<>(false);
		}
		return hasUserChosenDate.getValue() == null ? false : hasUserChosenDate.getValue();
	}

	//------------------------------------------------------------------------------------------------------


	/**
	 * Set whether the user has manually chosen a date.
	 *
	 * @param hasUserChosenDate The value to be set.
	 */
	public void setHasUserChosenDate(boolean hasUserChosenDate) {
		if (this.hasUserChosenDate == null) {
			this.hasUserChosenDate = new MutableLiveData<>();
		}
		this.hasUserChosenDate.setValue(hasUserChosenDate);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the alarm date and time. If {@code null}, throws a {@link NullPointerException}.
	 *
	 * @return Same as in description.
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	@NonNull
	public LocalDateTime getAlarmDateTime() {
		return alarmDateTime.getValue() == null ? LocalDateTime.now().plusHours(0) : Objects.requireNonNull(alarmDateTime.getValue(), "Alarm date-time was null.");
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the alarm date and time.
	 *
	 * @param alarmDateTime The value to be set. Cannot be null.
	 */
	public void setAlarmDateTime(@NonNull LocalDateTime alarmDateTime) {
		if (this.alarmDateTime == null) {
			this.alarmDateTime = new MutableLiveData<>();
		}
		this.alarmDateTime.setValue(alarmDateTime);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the snooze interval, i.e. the period after which the alarm should ring again. Returns 5 if not set previously.
	 *
	 * @return Same as in description.
	 */
	public int getSnoozeIntervalInMins() {
		if (snoozeIntervalInMins == null) {
			snoozeIntervalInMins = new MutableLiveData<>(1);
		}
		return snoozeIntervalInMins.getValue() == null ? 1 : snoozeIntervalInMins.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the snooze interval, i.e. the period after which the alarm should ring again.
	 *
	 * @param snoozeIntervalInMins The value to be set.
	 */
	public void setSnoozeIntervalInMins(int snoozeIntervalInMins) {
		if (this.snoozeIntervalInMins == null) {
			this.snoozeIntervalInMins = new MutableLiveData<>();
		}
		this.snoozeIntervalInMins.setValue(snoozeIntervalInMins);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the snooze interval, i.e. the period after which the alarm should ring again. Returns 5 if not set previously.
	 *
	 * @return Same as in description.
	 */
	public int getSnoozeIntervalInSecs() {
		if (snoozeIntervalInSecs == null) {
			snoozeIntervalInSecs = new MutableLiveData<>(3000);
		}
		return snoozeIntervalInSecs.getValue() == null ? 3000 : snoozeIntervalInSecs.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the snooze interval, i.e. the period after which the alarm should ring again.
	 *
	 * @param snoozeIntervalInSecs The value to be set.
	 */
	public void setSnoozeIntervalInSecs(int snoozeIntervalInSecs) {
		if (this.snoozeIntervalInSecs == null) {
			this.snoozeIntervalInSecs = new MutableLiveData<>();
		}
		this.snoozeIntervalInSecs.setValue(snoozeIntervalInSecs);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the number of times the alarm will be snoozed. Returns 3 if not set previously.
	 *
	 * @return Same as in description.
	 */
	public int getSnoozeFreq() {

		if (snoozeFreq == null) {
			snoozeFreq = new MutableLiveData<>(2);
		}
		return snoozeFreq.getValue() == null ? 2 : snoozeFreq.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the number of times the alarm will be snoozed before being dismissed.
	 *
	 * @param snoozeFreq The value to be set.
	 */
	public void setSnoozeFreq(int snoozeFreq) {
		if (this.snoozeFreq == null) {
			this.snoozeFreq = new MutableLiveData<>();
		}
		this.snoozeFreq.setValue(snoozeFreq);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the alarm type.
	 */
	public int getAlarmType() {
		if (alarmType == null) {
			alarmType = new MutableLiveData<>(0);
		}
		return alarmType.getValue() == null ? 0 : alarmType.getValue();

	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the alarm type.
	 *
	 */
	public void setAlarmType(int alarmType) {
		if (this.alarmType == null) {
			this.alarmType = new MutableLiveData<>();
		}
		this.alarmType.setValue(alarmType);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the alarm volume. Returns 3 if not set previously.
	 *
	 * @return Same as in description.
	 */
	public int getAlarmVolume(Context context) {
		if (alarmVolume == null) {
			alarmVolume = new MutableLiveData<>(((AudioManager) context.getSystemService(AUDIO_SERVICE)).getStreamMaxVolume(AudioManager.STREAM_ALARM));
		}
		return alarmVolume.getValue() == null ? 3 : alarmVolume.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the alarm volume.
	 *
	 * @param alarmVolume The value to be set.
	 */
	public void setAlarmVolume(int alarmVolume) {
		if (this.alarmVolume == null) {
			this.alarmVolume = new MutableLiveData<>();
		}
		this.alarmVolume.setValue(alarmVolume);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get whether snooze is ON or OFF. Default: {@code true}.
	 *
	 * @return {@code true} is snooze is ON, otherwise {@code false}. Default: {@code true}.
	 */
	@SuppressWarnings("SimplifiableConditionalExpression")
	public boolean getIsSnoozeOn() {
		if (isSnoozeOn == null) {
			isSnoozeOn = new MutableLiveData<>(true);
		}
		return isSnoozeOn.getValue() == null ? true : isSnoozeOn.getValue();

	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set whether snooze is ON or OFF.
	 *
	 * @param isSnoozeOn The value to be set.
	 */
	public void setIsSnoozeOn(boolean isSnoozeOn) {
		if (this.isSnoozeOn == null) {
			this.isSnoozeOn = new MutableLiveData<>();
		}
		this.isSnoozeOn.setValue(isSnoozeOn);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get whether repeat is ON or OFF. Default: {@code false}.
	 *
	 * @return {@code true} is repeat is ON, otherwise {@code false}. Default: {@code false}.
	 */
	@SuppressWarnings("SimplifiableConditionalExpression")
	public boolean getIsRepeatOn() {
		if (isRepeatOn == null) {
			isRepeatOn = new MutableLiveData<>(false);
		}
		return isRepeatOn.getValue() == null ? false : isRepeatOn.getValue();

	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set whether repeat is ON or OFF.
	 *
	 * @param isRepeatOn The value to be set.
	 */
	public void setIsRepeatOn(boolean isRepeatOn) {
		if (this.isRepeatOn == null) {
			this.isRepeatOn = new MutableLiveData<>();
		}
		this.isRepeatOn.setValue(isRepeatOn);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get whether the chosen date is same as today's date.
	 *
	 * @return Same as in description.
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public boolean getIsChosenDateToday() {
		if (isChosenDateToday == null) {
			isChosenDateToday = new MutableLiveData<>(getAlarmDateTime().toLocalDate().equals(LocalDate.now()));
		}
		return isChosenDateToday.getValue() == null ? getAlarmDateTime().toLocalDate().equals(LocalDate.now()) : isChosenDateToday.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set whether the chosen date is same as today.
	 *
	 * @param isChosenDateToday The value to be set.
	 */
	public void setIsChosenDateToday(boolean isChosenDateToday) {
		if (this.isChosenDateToday == null) {
			this.isChosenDateToday = new MutableLiveData<>();
		}
		this.isChosenDateToday.setValue(isChosenDateToday);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the alarm tone Uri.
	 *
	 * @return The alarm tone Uri.
	 */
	@NonNull
	public Uri getAlarmToneUri() {
		if (alarmToneUri == null) {
			alarmToneUri = new MutableLiveData<>(Settings.System.DEFAULT_ALARM_ALERT_URI);
		}
		return alarmToneUri.getValue() == null ? Settings.System.DEFAULT_ALARM_ALERT_URI : alarmToneUri.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the alarm tone Uri.
	 *
	 * @param alarmToneUri The value to be set.
	 */
	public void setAlarmToneUri(@NonNull Uri alarmToneUri) {
		if (this.alarmToneUri == null) {
			this.alarmToneUri = new MutableLiveData<>();
		}
		this.alarmToneUri.setValue(alarmToneUri);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the minimum date allowed.
	 * <p>
	 * This will be set as the min date for the calendar shown while choosing date.
	 *
	 * @return Same as in description.
	 */
	@NonNull
	public LocalDate getMinDate() {
		return Objects.requireNonNull(minDate.getValue(), "Minimum date was null.");
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the minimum allowed date. This will be set as the min date for the calendar shown while choosing date.
	 *
	 * @param minDate The value to be set.
	 */
	public void setMinDate(@NonNull LocalDate minDate) {
		if (this.minDate == null) {
			this.minDate = new MutableLiveData<>();
		}
		this.minDate.setValue(minDate);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the days on which the alarm should repeat.
	 *
	 * @return An ArrayList specifying the days on which the alarm should repeat. Follows {@link java.time.DayOfWeek} enum.
	 */
	@Nullable
	public int getRepeatDays() {
		if (repeatDays == null) {
			repeatDays = new MutableLiveData<>(2);
		}
		return repeatDays.getValue() == null ? 2 : repeatDays.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the days on which the alarm should repeat.
	 */
	public void setRepeatDays(@Nullable int repeatDays) {
		if (this.repeatDays == null) {
			this.repeatDays = new MutableLiveData<>();
		}
		this.repeatDays.setValue(repeatDays);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * The mode in which this activity has been opened.
	 *
	 * @return Either {@link Activity_AlarmDetails#MODE_EXISTING_ALARM} or {@link Activity_AlarmDetails#MODE_NEW_ALARM}.
	 */
	public int getMode() {
		if (mode == null) {
			mode = new MutableLiveData<>(Activity_AlarmDetails.MODE_NEW_ALARM);
		}
		return mode.getValue() == null ? Activity_AlarmDetails.MODE_NEW_ALARM : mode.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * The mode in which this activity has been opened.
	 *
	 * @param mode The value to be set. Either {@link Activity_AlarmDetails#MODE_EXISTING_ALARM} or {@link Activity_AlarmDetails#MODE_NEW_ALARM}.
	 */
	public void setMode(int mode) {
		if (this.mode == null) {
			this.mode = new MutableLiveData<>();
		}
		this.mode.setValue(mode);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the old alarm hour.
	 * <p>
	 * This should be called if and only if {@code mode} = {@link Activity_AlarmDetails#MODE_EXISTING_ALARM}. Otherwise this method will throw a
	 * {@code NullPointerException}.
	 * </p>
	 *
	 * @return The old alarm hour, if available. Otherwise throws {@code NullPointerException}.
	 */
	public int getOldAlarmHour() {
		if (oldAlarmHour == null || oldAlarmHour.getValue() == null) {
			throw new NullPointerException("Old alarm hour was null.");
		}
		return oldAlarmHour.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the old alarm hour.
	 * <p>
	 * This should be called if and only if {@code mode} = {@link Activity_AlarmDetails#MODE_EXISTING_ALARM}.
	 * </p>
	 *
	 * @param oldAlarmHour The value to be set.
	 */
	public void setOldAlarmHour(int oldAlarmHour) {
		if (this.oldAlarmHour == null) {
			this.oldAlarmHour = new MutableLiveData<>();
		}
		this.oldAlarmHour.setValue(oldAlarmHour);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get the old alarm minute.
	 * <p>
	 * This should be called if and only if {@code mode} = {@link Activity_AlarmDetails#MODE_EXISTING_ALARM}. Otherwise this method will throw a
	 * {@code NullPointerException}.
	 * </p>
	 *
	 * @return The old alarm minute, if available. Otherwise throws {@code NullPointerException}.
	 */
	public int getOldAlarmMinute() {
		if (oldAlarmMinute == null || oldAlarmMinute.getValue() == null) {
			throw new NullPointerException("Old alarm minute was null.");
		}
		return oldAlarmMinute.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the old alarm minute.
	 * <p>
	 * This should be called if and only if {@code mode} = {@link Activity_AlarmDetails#MODE_EXISTING_ALARM}.
	 * </p>
	 *
	 * @param oldAlarmMinute The value to be set.
	 */
	public void setOldAlarmMinute(int oldAlarmMinute) {
		if (this.oldAlarmMinute == null) {
			this.oldAlarmMinute = new MutableLiveData<>();
		}
		this.oldAlarmMinute.setValue(oldAlarmMinute);
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get an observable instance of alarm volume.
	 *
	 * @return Same as in description.
	 */
	public LiveData<Integer> getLiveAlarmVolume() {
		return alarmVolume;
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get an observable instance of whether repeat is ON or OFF.
	 *
	 * @return Same as in description.
	 */
	public LiveData<Boolean> getLiveIsRepeatOn() {
		return isRepeatOn;
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Get an observable instance of the alarm type.
	 *
	 * @return Same as in description.
	 */
	public LiveData<Integer> getLiveAlarmType() {
		return alarmType;
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Returns the alarm message.
	 *
	 * @return Same as in description.
	 */
	@Nullable
	public String getAlarmMessage() {
		if (alarmMessage == null) {
			alarmMessage = new MutableLiveData<>(null);
		}
		return alarmMessage.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the alarm message.
	 *
	 * @param alarmMessage The alarm message to be set. May be {@code null}.
	 */
	public void setAlarmMessage(@Nullable String alarmMessage) {
		if (this.alarmMessage == null) {
			this.alarmMessage = new MutableLiveData<>();
		}
		this.alarmMessage.setValue(alarmMessage);
	}
	/**
	 * Returns the alarmId.
	 *
	 * @return Same as in description.
	 */
	@Nullable
	public Integer getAlarmId() {
		if (alarmId == null) {
			alarmId = new MutableLiveData<>(null);
		}
		return alarmId.getValue();
	}

	//------------------------------------------------------------------------------------------------------

	/**
	 * Set the alarmId.
	 *
	 * @param alarmId The alarm message to be set. May be {@code null}.
	 */
	public void setAlarmId(@Nullable Integer alarmId) {
		if (this.alarmId == null) {
			this.alarmId = new MutableLiveData<>();
		}
		this.alarmId.setValue(alarmId);
	}

	/**
	 * Returns whether there is a pending alarm that has to be switched on.
	 * <p>
	 * The alarm is pending because {@link android.Manifest.permission#SCHEDULE_EXACT_ALARM} has not been granted to the app.
	 *
	 * @return {@code true} if an alarm is pending to be switched on, otherwise {@code false}.
	 */
	public boolean getPendingStatus() {
		return isAlarmPending.getValue() != null && isAlarmPending.getValue();
	}

	//--------------------------------------------------------------------------------------------------

	/**
	 * Set whether an alarm is pending to be switched on.
	 * <p>
	 * The alarm is pending because {@link android.Manifest.permission#SCHEDULE_EXACT_ALARM} has not been granted to the app.
	 *
	 * @param status {@code true} if an alarm is pending to be switched on, otherwise {@code false}.
	 */
	public void setPendingStatus(boolean status) {
		isAlarmPending.setValue(status);
	}

	//--------------------------------------------------------------------------------------------------

	/**
	 * Save the details of a pending alarm.
	 *
	 * @param data The details of the pending alarm. May be {@code null}.
	 */
	public void savePendingAlarm(@Nullable Bundle data) {
		pendingAlarmDetails = new MutableLiveData<>();
		pendingAlarmDetails.setValue(data);
	}

	//--------------------------------------------------------------------------------------------------

	/**
	 * Get the details of a pending alarm.
	 *
	 * @return A {@link Bundle} with the details of the pending alarm.
	 */
	@Nullable
	public Bundle getPendingALarmData() {
		return pendingAlarmDetails.getValue();
	}

	public void setIsSettingsActOver(boolean isSettingsActOver) {
		this.isSettingsActOver.setValue(isSettingsActOver);
	}

	//--------------------------------------------------------------------------------------------------

	public boolean getIsSettingsActOver() {
		return Objects.requireNonNull(isSettingsActOver.getValue());
	}
}
