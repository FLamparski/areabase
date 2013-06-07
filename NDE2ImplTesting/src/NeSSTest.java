import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.methodcalls.discovery.FindAreasMethodCall;
import nde2.types.discovery.Area;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xml.sax.SAXException;

public class NeSSTest {

	public enum FindAreasBy {
		AREA_ID, POSTCODE, NAME_PART
	}

	private interface NDEThreadOperationCallbacks {
		public boolean onUpdate(int newProgress, String newStatus);

		public void onFinish(List<Area> result);

		public void onError(Exception cause, String msg);
	}

	private FindAreasBy m_findAreasByWhat;

	protected Shell mNdeTestAppShell;
	private Text mAreaQueryTextField;
	private Button mFindAreasByNamePartBtn;
	private Button mFindAreasByPostCodeBtn;
	private Button mFindAreasByIdBtn;
	private Button mFindAreasBtn;
	private CTabFolder mAreasTabFolder;
	private Tree mAreaHierarchyTree;
	private ProgressBar mOperationPBar;
	private Label mStatusLabel;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NeSSTest window = new NeSSTest();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		while (!mNdeTestAppShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		mNdeTestAppShell = new Shell();
		mNdeTestAppShell.setText("NDE2 Query Tool");
		mNdeTestAppShell.setSize(664, 522);
		mNdeTestAppShell.setLayout(new GridLayout(2, false));

		Group mFindAreasGroup = new Group(mNdeTestAppShell, SWT.NONE);
		GridData gd_mFindAreasGroup = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_mFindAreasGroup.widthHint = 241;
		mFindAreasGroup.setLayoutData(gd_mFindAreasGroup);
		mFindAreasGroup.setText("Find areas...");

		mFindAreasByNamePartBtn = new Button(mFindAreasGroup, SWT.RADIO);
		mFindAreasByNamePartBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_findAreasByWhat = FindAreasBy.NAME_PART;
			}
		});
		mFindAreasByNamePartBtn.setBounds(10, 51, 114, 24);
		mFindAreasByNamePartBtn.setText("Name part");

		mFindAreasByPostCodeBtn = new Button(mFindAreasGroup, SWT.RADIO);
		mFindAreasByPostCodeBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_findAreasByWhat = FindAreasBy.POSTCODE;
			}
		});
		mFindAreasByPostCodeBtn.setBounds(10, 21, 114, 24);
		mFindAreasByPostCodeBtn.setText("Postcode");

		mFindAreasByIdBtn = new Button(mFindAreasGroup, SWT.RADIO);
		mFindAreasByIdBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_findAreasByWhat = FindAreasBy.AREA_ID;
			}
		});
		mFindAreasByIdBtn.setBounds(10, 81, 114, 24);
		mFindAreasByIdBtn.setText("AreaId");

		mAreaQueryTextField = new Text(mFindAreasGroup, SWT.BORDER);
		mAreaQueryTextField.setBounds(10, 111, 225, 27);

		mFindAreasBtn = new Button(mFindAreasGroup, SWT.NONE);
		mFindAreasBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				findAreasAndFillTree();
			}
		});
		mFindAreasBtn.setBounds(144, 142, 91, 29);
		mFindAreasBtn.setText("Go!");
		mFindAreasGroup.setTabList(new Control[] { mFindAreasByPostCodeBtn,
				mFindAreasByNamePartBtn, mFindAreasByIdBtn,
				mAreaQueryTextField, mFindAreasBtn });

		mAreasTabFolder = new CTabFolder(mNdeTestAppShell, SWT.BORDER);
		GridData gd_mAreasTabFolder = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 2);
		gd_mAreasTabFolder.widthHint = 384;
		mAreasTabFolder.setLayoutData(gd_mAreasTabFolder);
		mAreasTabFolder.setSelectionBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		Group mFoundAreasHierarchyGroup = new Group(mNdeTestAppShell, SWT.NONE);
		GridData gd_mFoundAreasHierarchyGroup = new GridData(SWT.FILL,
				SWT.CENTER, false, false, 1, 1);
		gd_mFoundAreasHierarchyGroup.heightHint = 255;
		mFoundAreasHierarchyGroup.setLayoutData(gd_mFoundAreasHierarchyGroup);
		mFoundAreasHierarchyGroup.setText("Select area to view...");

		mAreaHierarchyTree = new Tree(mFoundAreasHierarchyGroup, SWT.BORDER);
		mAreaHierarchyTree.setBounds(10, 24, 227, 240);
		mAreaHierarchyTree.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selection = mAreaHierarchyTree.getSelection();
				for (TreeItem item : selection) {
					Area a = (Area) item.getData("AREA");
					String n = item.getText();
					System.out.println(String.format(
							"Selected: %s; Area={ id: %d, name: %s }", n,
							a.getAreaId(), a.getName()));
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				TreeItem[] selection = mAreaHierarchyTree.getSelection();
				for (TreeItem item : selection) {
					Area a = (Area) item.getData("AREA");
					String n = item.getText();
					System.out.println(String.format(
							"DefaultSelected: %s; Area={ id: %d, name: %s }",
							n, a.getAreaId(), a.getName()));
				}

				Area selectedArea = (Area) selection[0].getData("AREA");
				CTabItem selectedAreaTabItem = new CTabItem(mAreasTabFolder,
						SWT.NONE);
				selectedAreaTabItem.setText(selectedArea.getName());
				selectedAreaTabItem.setData("AREA", selectedArea);
				selectedAreaTabItem.setShowClose(true);
			}
		});

		Label lblStatus = new Label(mNdeTestAppShell, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		lblStatus.setText("Status");
		lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));

		mOperationPBar = new ProgressBar(mNdeTestAppShell, SWT.NONE);
		mOperationPBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		mStatusLabel = new Label(mNdeTestAppShell, SWT.NONE);
		mStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		mStatusLabel.setText("Ready.");
		mNdeTestAppShell.setTabList(new Control[] { mFindAreasGroup,
				mFoundAreasHierarchyGroup, mAreasTabFolder });

		mNdeTestAppShell.open();
	}

	protected void findAreasAndFillTree() {
		mStatusLabel.setText("Querying the service...");
		mOperationPBar.setSelection(1);

		final String qString = mAreaQueryTextField.getText();
		final FindAreasBy fBw = m_findAreasByWhat;
		final NDEThreadOperationCallbacks updateCallbacks = new NDEThreadOperationCallbacks() {

			@Override
			public void onFinish(List<Area> result) {
				mAreaHierarchyTree.clearAll(true);
				TreeItem previousTiRef = null;
				for (int i = 0; i < result.size(); i++) {
					TreeItem treeItem;
					if (m_findAreasByWhat == FindAreasBy.POSTCODE
							&& previousTiRef != null) {
						treeItem = new TreeItem(previousTiRef, SWT.NONE);
					} else {
						treeItem = new TreeItem(mAreaHierarchyTree, SWT.NONE);
					}
					treeItem.setText(result.get(i).getName());
					treeItem.setData("AREA", result.get(i));

					previousTiRef = treeItem;

					int newProgress = 80 + ((20 * i) / (result.size() - 1));
					mOperationPBar.setSelection(newProgress);
				}
				mOperationPBar.setSelection(0);
				mStatusLabel.setText("Ready.");
			}

			@Override
			public void onError(Exception cause, String msg) {
				mOperationPBar.setSelection(0);
				// TODO: Create a dialogue box for errors.
			}

			@Override
			public boolean onUpdate(int newProgress, String newStatus) {
				mOperationPBar.setSelection(newProgress);
				mStatusLabel.setText(newStatus);
				return true;
			}
		};

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				List<Area> retList = null;
				FindAreasMethodCall methodCall = new FindAreasMethodCall();
				switch (fBw) {
				case AREA_ID:
					methodCall.addCode(qString);
					break;
				case NAME_PART:
					methodCall.addAreaNamePart(qString);
					break;
				case POSTCODE:
					methodCall.addPostcode(qString);
					break;
				default:
					updateCallbacks.onError(null,
							"Lookup method not specified.");
					return;
				}
				updateCallbacks.onUpdate(20, "Looking up areas...");
				try {
					retList = methodCall.findAreas();
				} catch (XPathExpressionException | NDE2Exception
						| ParserConfigurationException | SAXException
						| IOException e) {
					e.printStackTrace();
					updateCallbacks.onError(e,
							"An exception was thrown when querying NDE.");
					return;
				}
				updateCallbacks.onUpdate(80, "Processing areas...");
				updateCallbacks.onFinish(retList);
				return;
			}
		});
	}
}
