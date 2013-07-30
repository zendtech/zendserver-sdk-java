package org.zend.webapi.test.server;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.junit.Assert;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.routing.Router;
import org.w3c.dom.Document;
import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.test.server.response.FileResponse;
import org.zend.webapi.test.server.response.ServerResponse;
import org.zend.webapi.test.server.response.ServiceResponse;

public class ServerApplication extends Application {

	private String base;

	public ServerApplication(SystemEdition edition) {
		switch (edition) {
		case ZEND_SERVER_CLUSER_MANAGER:
		case ZEND_SERVER_COMMUNITY_EDITION:
			this.base = "/ZendServerManager/";
			break;
		default:
			this.base = "/ZendServer/";
			break;
		}
	}
	
	@Override
	public Restlet createInboundRoot() {
		return createRoot();
	}

	public synchronized Restlet createRoot() {
		Router router = new Router(getContext());

		Restlet getSystemInfo = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.getSystemInfo();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet clusterGetServerStatus = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.clusterGetServerStatus();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet clusterAddServer = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				String postParams = request.getEntityAsText();
				Assert.assertNotNull(postParams);
				ServerResponse serverResponse = ZendSystem.getInstance()
						.clusterAddServer();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet clusterRemoveServer = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				String postParams = request.getEntityAsText();
				Assert.assertNotNull(postParams);
				ServerResponse serverResponse = ZendSystem.getInstance()
						.clusterRemoveServer();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet clusterDisableServer = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				String postParams = request.getEntityAsText();
				Assert.assertNotNull(postParams);
				ServerResponse serverResponse = ZendSystem.getInstance()
						.clusterDisableServer();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet clusterEnableServer = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				String postParams = request.getEntityAsText();
				Assert.assertNotNull(postParams);
				ServerResponse serverResponse = ZendSystem.getInstance()
						.clusterEnableServer();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet restartPhp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.restartPhp();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet configurationExport = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				FileResponse serverResponse = (FileResponse) ZendSystem
						.getInstance().configurationExport();
				InputRepresentation representation = new InputRepresentation(
						new ByteArrayInputStream(serverResponse.getContent()),
						MediaType.valueOf("application/vnd.zend.serverconfig"));
				Disposition disposition = new Disposition(
						Disposition.TYPE_ATTACHMENT);
				disposition.setFilename(serverResponse.getFileName());
				representation.setDisposition(disposition);
				representation.setSize(serverResponse.getFileSize());
				response.setEntity(representation);
				response.setStatus(serverResponse.getStatus());
			}
		};

		Restlet configurationImport = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				final ServerResponse serverResponse = ZendSystem.getInstance()
						.configurationImport();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet applicationGetStatus = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.applicationGetStatus();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet applicationDeploy = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.applicationDeploy();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet applicationUpdate = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.applicationUpdate();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet applicationRemove = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.applicationRemove();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet applicationRedeploy = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.applicationRedeploy();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet clusterReconfigureServer = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.clusterReconfigureServer();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet applicationRollback = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.applicationRollback();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet codeTracingDisable = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.codeTracingDisable();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet codeTracingEnable = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.codeTracingEnable();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet codeTracingIsEnabled = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.codeTracingIsEnabled();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet codeTracingCreate = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.codeTracingCreate();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet codeTracingList = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.codeTracingList();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet codeTracingDelete = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.codeTracingDelete();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet codetracingDownloadTraceFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				FileResponse serverResponse = (FileResponse) ZendSystem
						.getInstance().codetracingDownloadTraceFile();
				InputRepresentation representation = new InputRepresentation(
						new ByteArrayInputStream(serverResponse.getContent()),
						MediaType.valueOf("application/vnd.zend.serverconfig"));
				Disposition disposition = new Disposition(
						Disposition.TYPE_ATTACHMENT);
				disposition.setFilename(serverResponse.getFileName());
				representation.setDisposition(disposition);
				representation.setSize(serverResponse.getFileSize());
				response.setEntity(representation);
				response.setStatus(serverResponse.getStatus());
			}
		};

		Restlet monitorGetRequestSummary = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.monitorGetRequestSummary();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet monitorGetIssuesListPredefinedFilter = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.monitorGetIssuesListByPredefinedFilter();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet monitorGetIssueDetails = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.monitorGetIssueDetails();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet monitorGetEventGroupDetails = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.monitorGetEventGroupDetails();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet monitorExportIssueByEventsGroup = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				FileResponse serverResponse = (FileResponse) ZendSystem
						.getInstance().monitorExportIssueByEventsGroup();
				InputRepresentation representation = new InputRepresentation(
						new ByteArrayInputStream(serverResponse.getContent()),
						MediaType.valueOf("application/vnd.zend.eventexport"));
				Disposition disposition = new Disposition(
						Disposition.TYPE_ATTACHMENT);
				disposition.setFilename(serverResponse.getFileName());
				representation.setDisposition(disposition);
				representation.setSize(serverResponse.getFileSize());
				response.setEntity(representation);
				response.setStatus(serverResponse.getStatus());
			}
		};

		Restlet monitorChangeIssueStatus = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.monitorChangeIssueStatus();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet studioStartDebug = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.studioStartDebug();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet studioStartProfile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.studioStartProfile();
				prepareResponse(response, serverResponse);
			}
		};
		Restlet libraryGetStatus = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.libraryGetStatus();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet libraryVersionGetStatus = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.libraryVersionGetStatus();
				prepareResponse(response, serverResponse);
			}
		};

		Restlet libraryVersionDeploy = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.libraryVersionDeploy();
				prepareResponse(response, serverResponse);
			}
		};
		
		Restlet librarySynchronize = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				ServerResponse serverResponse = ZendSystem.getInstance()
						.librarySynchronize();
				prepareResponse(response, serverResponse);
			}
		};
		
		Restlet downloadLibraryVersionFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				FileResponse serverResponse = (FileResponse) ZendSystem
						.getInstance().downloadLibraryVersionFile();
				InputRepresentation representation = new InputRepresentation(
						new ByteArrayInputStream(serverResponse.getContent()),
						MediaType.valueOf("application/vnd.zend.zpk"));
				Disposition disposition = new Disposition(
						Disposition.TYPE_ATTACHMENT);
				disposition.setFilename(serverResponse.getFileName());
				representation.setDisposition(disposition);
				representation.setSize(serverResponse.getFileSize());
				response.setEntity(representation);
				response.setStatus(serverResponse.getStatus());
			}
		};

		router.attach(base + "Api/getSystemInfo", getSystemInfo);
		router.attach(base + "Api/clusterGetServerStatus",
				clusterGetServerStatus);
		router.attach(base + "Api/clusterAddServer", clusterAddServer);
		router.attach(base + "Api/clusterRemoveServer", clusterRemoveServer);
		router.attach(base + "Api/clusterDisableServer", clusterDisableServer);
		router.attach(base + "Api/clusterEnableServer", clusterEnableServer);
		router.attach(base + "Api/clusterReconfigureServer",
				clusterReconfigureServer);
		router.attach(base + "Api/restartPhp", restartPhp);
		router.attach(base + "Api/configurationExport", configurationExport);
		router.attach(base + "Api/configurationImport", configurationImport);
		router.attach(base + "Api/applicationGetStatus", applicationGetStatus);
		router.attach(base + "Api/applicationDeploy", applicationDeploy);
		router.attach(base + "Api/applicationUpdate", applicationUpdate);
		router.attach(base + "Api/applicationRemove", applicationRemove);
		router.attach(base + "Api/applicationSynchronize", applicationRedeploy);
		router.attach(base + "Api/applicationRollback", applicationRollback);
		router.attach(base + "Api/codetracingDisable", codeTracingDisable);
		router.attach(base + "Api/codetracingEnable", codeTracingEnable);
		router.attach(base + "Api/codetracingIsEnabled", codeTracingIsEnabled);
		router.attach(base + "Api/codetracingCreate", codeTracingCreate);
		router.attach(base + "Api/codetracingDelete", codeTracingDelete);
		router.attach(base + "Api/codetracingList", codeTracingList);
		router.attach(base + "Api/codetracingDownloadTraceFile",
				codetracingDownloadTraceFile);
		router.attach(base + "Api/monitorGetRequestSummary",
				monitorGetRequestSummary);
		router.attach(base + "Api/monitorGetIssuesListPredefinedFilter",
				monitorGetIssuesListPredefinedFilter);
		router.attach(base + "Api/monitorGetIssueDetails",
				monitorGetIssueDetails);
		router.attach(base + "Api/monitorGetEventGroupDetails",
				monitorGetEventGroupDetails);
		router.attach(base + "Api/monitorExportIssueByEventsGroup",
				monitorExportIssueByEventsGroup);
		router.attach(base + "Api/monitorChangeIssueStatus",
				monitorChangeIssueStatus);
		router.attach(base + "Api/studioStartDebug", studioStartDebug);
		router.attach(base + "Api/studioStartProfile", studioStartProfile);
		router.attach(base + "Api/libraryGetStatus", libraryGetStatus);
		router.attach(base + "Api/libraryVersionGetStatus",
				libraryVersionGetStatus);
		router.attach(base + "Api/libraryVersionDeploy", libraryVersionDeploy);
		router.attach(base + "Api/librarySynchronize", librarySynchronize);
		router.attach(base + "Api/downloadLibraryVersionFile",
				downloadLibraryVersionFile);
		return router;
	}

	private void prepareResponse(Response response,
			ServerResponse serverResponse) {
		Document doc = ((ServiceResponse) serverResponse).getData();
		Representation representation = new DomRepresentation(
				MediaType.register("application/vnd.zend.serverapi",
						"version=1.1"), doc);

		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer xform = factory.newTransformer();
			Source src = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			Result result = new javax.xml.transform.stream.StreamResult(writer);
			xform.transform(src, result);
			representation.setSize(writer.toString().length());
		} catch (TransformerConfigurationException e) {
			// ignore
		} catch (TransformerException e) {
			// ignore
		}
		response.setEntity(representation);
		response.setStatus(serverResponse.getStatus());
	}
}