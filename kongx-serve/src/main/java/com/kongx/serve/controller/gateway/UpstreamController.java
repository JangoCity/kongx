package com.kongx.serve.controller.gateway;

import com.kongx.common.aop.PreAuthorize;
import com.kongx.common.core.entity.UserInfo;
import com.kongx.common.jsonwrapper.JsonHeaderWrapper;
import com.kongx.serve.controller.BaseController;
import com.kongx.serve.entity.gateway.KongEntity;
import com.kongx.serve.entity.gateway.Upstream;
import com.kongx.serve.entity.system.OperationLog;
import com.kongx.serve.service.gateway.UpstreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController("/upstreamController")
@RequestMapping("/kong/api/")
@Import(FeignClientsConfiguration.class)
@Slf4j
public class UpstreamController extends BaseController {
    private static final String UPSTREAM_URI = "/upstreams";
    private static final String UPSTREAM_URI_ID = "/upstreams/{id}";

    @Autowired
    private UpstreamService upstreamService;

    /**
     * 查询所有upstream
     *
     * @return
     */
    @RequestMapping(value = UPSTREAM_URI, method = RequestMethod.GET)
    @PreAuthorize("upstream_view")
    public JsonHeaderWrapper findAll(UserInfo userInfo) {
        JsonHeaderWrapper jsonHeaderWrapper = this.init();
        try {
            KongEntity<Upstream> upstreamKongEntity = upstreamService.findAll(systemProfile(userInfo));
            jsonHeaderWrapper.setData(upstreamKongEntity.getData());
        } catch (Exception e) {
            jsonHeaderWrapper.setStatus(JsonHeaderWrapper.StatusEnum.Failed.getCode());
            jsonHeaderWrapper.setErrmsg(e.getMessage());
        }
        return jsonHeaderWrapper;
    }

    /**
     * 新增upstream
     *
     * @param upstream
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = UPSTREAM_URI, method = RequestMethod.POST)
    @PreAuthorize("upstream_add")
    public JsonHeaderWrapper addUpstream(UserInfo userInfo, @RequestBody Upstream upstream) {
        JsonHeaderWrapper jsonHeaderWrapper = this.init();
        try {
            Upstream results = this.upstreamService.add(systemProfile(userInfo), upstream.clear());
            jsonHeaderWrapper.setData(results);
            this.log(userInfo, OperationLog.OperationType.OPERATION_ADD, OperationLog.OperationTarget.UPSTREAM, upstream, upstream.getName());
        } catch (Exception e) {
            jsonHeaderWrapper.setStatus(JsonHeaderWrapper.StatusEnum.Failed.getCode());
            jsonHeaderWrapper.setErrmsg(e.getMessage());
        }
        return jsonHeaderWrapper;
    }

    /**
     * 更新upstream
     *
     * @param id
     * @param upstream
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = UPSTREAM_URI_ID, method = RequestMethod.POST)
    public JsonHeaderWrapper update(UserInfo userInfo, @PathVariable String id, @RequestBody Upstream upstream) {
        JsonHeaderWrapper jsonHeaderWrapper = this.init();
        try {
            Upstream results = this.upstreamService.update(systemProfile(userInfo), id, upstream.clear());
            jsonHeaderWrapper.setData(results);
            this.log(userInfo, OperationLog.OperationType.OPERATION_UPDATE, OperationLog.OperationTarget.UPSTREAM, upstream, upstream.getName());
        } catch (Exception e) {
            jsonHeaderWrapper.setStatus(JsonHeaderWrapper.StatusEnum.Failed.getCode());
            jsonHeaderWrapper.setErrmsg(e.getMessage());
        }
        return jsonHeaderWrapper;
    }

    /**
     * 删除upstream
     *
     * @param id
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = UPSTREAM_URI_ID, method = RequestMethod.DELETE)
    public JsonHeaderWrapper remove(UserInfo userInfo, @PathVariable String id) throws Exception {
        JsonHeaderWrapper jsonHeaderWrapper = this.init();
        try {
            Upstream upstream = this.upstreamService.findUpstream(systemProfile(userInfo), id);
            KongEntity<Upstream> upstreamKongEntity = this.upstreamService.remove(systemProfile(userInfo), id);
            this.log(userInfo, OperationLog.OperationType.OPERATION_DELETE, OperationLog.OperationTarget.UPSTREAM, upstream, upstream.getName());
            jsonHeaderWrapper.setData(upstreamKongEntity.getData());
        } catch (Exception e) {
            jsonHeaderWrapper.setStatus(JsonHeaderWrapper.StatusEnum.Failed.getCode());
            jsonHeaderWrapper.setErrmsg(e.getMessage());
        }
        return jsonHeaderWrapper;
    }

    /**
     * 查询单个upstream的信息
     *
     * @param id
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = UPSTREAM_URI_ID, method = RequestMethod.GET)
    public JsonHeaderWrapper findUpstream(UserInfo userInfo, @PathVariable String id) {
        JsonHeaderWrapper jsonHeaderWrapper = this.init();
        try {
            Upstream results = this.upstreamService.findUpstream(systemProfile(userInfo), id);
            jsonHeaderWrapper.setData(results);
        } catch (Exception e) {
            jsonHeaderWrapper.setStatus(JsonHeaderWrapper.StatusEnum.Failed.getCode());
            jsonHeaderWrapper.setErrmsg(e.getMessage());
        }
        return jsonHeaderWrapper;
    }
}
