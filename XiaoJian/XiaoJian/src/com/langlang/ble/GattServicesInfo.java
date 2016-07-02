package com.langlang.ble;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

/**
 * This class saves gatt services and characteristics info.
 * 
 * @author Henry
 * 
 */
@SuppressLint("NewApi")
public class GattServicesInfo implements Serializable {

	/**
	 * serial id.
	 */
	private static final long serialVersionUID = 278970856415348306L;
	public static final String LIST_NAME = "NAME";
	public static final String LIST_UUID = "UUID";
	ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
	ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
	ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	ArrayList<String> serviceUUIDList = new ArrayList<String>();

	/**
	 * Constructor of this class.
	 * 
	 * @param gattServices
	 *            list of gatt services.
	 * @param resources
	 *            available resources object to provide resource access, allows
	 *            null.
	 */
	public GattServicesInfo(List<BluetoothGattService> gattServices,
			Resources resources) {
		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = "unknown service";
		String unknownCharaString = "unknown_characteristic";
		if (resources != null) {
			try {
				// unknownServiceString = resources.getString(
				// R.string.unknown_service);
				// unknownCharaString = resources.getString(
				// R.string.unknown_characteristic);
			} catch (NotFoundException e) {
				// Do nothing.
			}
		}
		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			serviceUUIDList.add(uuid);
			currentServiceData.put(LIST_NAME,
					SampleGattAttributes.lookup(uuid, unknownServiceString));
			currentServiceData.put(LIST_UUID, uuid);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();
				currentCharaData.put(LIST_NAME,
						SampleGattAttributes.lookup(uuid, unknownCharaString));
				currentCharaData.put(LIST_UUID, uuid);
				gattCharacteristicGroupData.add(currentCharaData);
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData);
		}
	}

	/**
	 * Return available gatt services info list, each element of list is map
	 * object and save service Name and service UUID.
	 * 
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getServiceList() {
		return gattServiceData;
	}

	/**
	 * Return list of all characteristic info, each element of top list saves
	 * all characteristics info of relative gatt service, the index of each
	 * element is relative to gatt service indexed and indicated in
	 * <code>gattServiceData</code> list object.
	 * <p>
	 * Each element of top list is a list of map to save each gatt
	 * characteristic name and UUID.
	 * 
	 * @return
	 */

	public ArrayList<ArrayList<HashMap<String, String>>> getCharacteristicList() {
		return gattCharacteristicData;
	}

	/**
	 * Return all available gatt characteristic instances, the index of each
	 * element of top list is relative to gatt service indexed and indicated in
	 * <code>gattServiceData</code> list object.
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<BluetoothGattCharacteristic>> getHierarchicalGattCharacteristicsList() {
		return mGattCharacteristics;
	}

	/**
	 * Return all gatt characteristic instances of specified service.
	 * 
	 * @param servicePosition
	 * @return
	 */
	public ArrayList<BluetoothGattCharacteristic> getGattCharacteristicsList(
			int servicePosition) {
		return mGattCharacteristics.get(servicePosition);
	}

	/**
	 * Return gatt characteristic instance by specified service position and
	 * characteristic position.
	 * 
	 * @param servicePosition
	 * @param charaPosition
	 * @return
	 */
	public BluetoothGattCharacteristic getGattCharacteristic(
			int servicePosition, int charaPosition) {
		return getGattCharacteristicsList(servicePosition).get(charaPosition);
	}

	/**
	 * Return all gatt characteristic instances of specified service.
	 * 
	 * @param serviceUUID
	 * @param charaUUID
	 * @return
	 */
	public ArrayList<BluetoothGattCharacteristic> getGattCharacteristicList(
			String serviceUUID) {
		int sUuid = serviceUUIDList.indexOf(serviceUUID);
		if (sUuid >= 0) {
			return mGattCharacteristics.get(sUuid);
		}
		return null;
	}

	/**
	 * Return gatt characteristic instance by specified service UUID and
	 * characteristic UUID.
	 * 
	 * @param serviceUUID
	 * @param charaUUID
	 * @return
	 */
	public BluetoothGattCharacteristic getGattCharacteristic(
			String serviceUUID, String charaUUID) {
		ArrayList<BluetoothGattCharacteristic> charaList = getGattCharacteristicList(serviceUUID);
		if (charaList != null) {
			for (BluetoothGattCharacteristic bgc : charaList) {
				if (bgc.getUuid().equals(charaUUID)) {
					return bgc;
				}
			}
		}
		return null;
	}
}
