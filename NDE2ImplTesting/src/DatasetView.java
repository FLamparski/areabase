import java.util.List;

import nde2.types.delivery.Dataset;
import nde2.types.discovery.DataSetFamiliy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

public class DatasetView extends Composite {
	private Combo datasetSelectorCombo;
	private Button fetchDatasetsBtn;
	private Table datasetViewTable;

	private List<DataSetFamiliy> datasetFamilies;
	private List<Dataset> datasets;

	public interface OnFetchListener {
		public List<DataSetFamiliy> onFetch();
	}

	public interface OnSelectDatasetListener {
		public List<Dataset> onSelectDataset();
	}

	public interface NDEGetDatasetFamiliesThreadOperationCallbacks {
		public boolean onUpdate(int newProgress, String newStatus);

		public void onFinish(List<DataSetFamiliy> result);

		public void onError(Exception cause, String msg);
	}

	public interface NDEGetTablesThreadOperationCallbacks {
		public boolean onUpdate(int newProgress, String newStatus);

		public void onFinish(List<DataSetFamiliy> result);

		public void onError(Exception cause, String msg);
	}

	private OnFetchListener onFetchListener;
	private OnSelectDatasetListener onSelectDatasetListener;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DatasetView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));

		Label lblDatasetFamily = new Label(this, SWT.NONE);
		lblDatasetFamily.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblDatasetFamily.setText("Dataset Family:");

		datasetSelectorCombo = new Combo(this, SWT.NONE);
		datasetSelectorCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		datasetSelectorCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		fetchDatasetsBtn = new Button(this, SWT.NONE);
		fetchDatasetsBtn.setText("Fetch");
		fetchDatasetsBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				datasetFamilies = onFetchListener.onFetch();
				String[] dsNames = new String[datasetFamilies.size()];
				for (int i = 0; i < datasetFamilies.size(); i++) {
					dsNames[i] = datasetFamilies.get(i).getName();
				}
				datasetSelectorCombo.setItems(dsNames);
			}
		});

		datasetViewTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		datasetViewTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 3, 1));
		datasetViewTable.setHeaderVisible(true);
		datasetViewTable.setLinesVisible(true);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @param onFetchListener
	 *            the onFetchListener to set
	 */
	public void setOnFetchListener(OnFetchListener onFetchListener) {
		this.onFetchListener = onFetchListener;
	}

	/**
	 * @param onSelectDatasetListener
	 *            the onSelectDatasetListener to set
	 */
	public void setOnSelectDatasetListener(
			OnSelectDatasetListener onSelectDatasetListener) {
		this.onSelectDatasetListener = onSelectDatasetListener;
	}

	/**
	 * @return the datasetSelectorCombo
	 */
	public Combo getDatasetSelectorCombo() {
		return datasetSelectorCombo;
	}

	/**
	 * @return the fetchDatasetsBtn
	 */
	public Button getFetchDatasetsBtn() {
		return fetchDatasetsBtn;
	}

	/**
	 * @return the datasetViewTable
	 */
	public Table getDatasetViewTable() {
		return datasetViewTable;
	}

}
