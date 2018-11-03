package sort;

public class Quicksort {
	public static void sort(int[] arr) {
		if (arr == null || arr.length == 0) {
			return;
		}
		sort(arr, 0, arr.length - 1);
	}

	private static void sort(int arr[], int first, int last) {
		if (first < last) {
			int partitionIndex = partition(arr, first, last);

			sort(arr, first, partitionIndex - 1);
			sort(arr, partitionIndex + 1, last);
		}
	}

	private static int partition(int arr[], int first, int last) {
		int pivot = arr[last];
		int i = first;
		for (int j = first; j < last; j++) {
			if (arr[j] <= pivot) {
				swap(arr, i, j);
				i++;
			}
		}

		swap(arr, i, last);

		return i;
	}

	private static void swap(int[] values, int i, int j) {
		int temp = values[i];
		values[i] = values[j];
		values[j] = temp;
	}
}