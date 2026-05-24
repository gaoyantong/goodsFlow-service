package com.goodsflow.admin.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.base.utils.BaseDataExcelUtils;
import com.goodsflow.admin.base.vo.GoodsSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.base.entity.Goods;
import com.goodsflow.dao.base.service.IGoodsService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/base/goods")
public class GoodsController {
    private static final String[] GOODS_HEADERS = {"货品ID", "通用名", "生产厂商", "规格", "单位"};

    private final IGoodsService goodsService;

    public GoodsController(IGoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("list")
    public ResData<List<Goods>> list(@RequestBody GoodsSearchParams params) {
        LambdaQueryWrapper<Goods> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Goods::getDeleted, false)
            .like(StringUtils.hasText(params.getGoodsId()), Goods::getGoodsId, params.getGoodsId())
            .like(StringUtils.hasText(params.getGenericName()), Goods::getGenericName, params.getGenericName())
            .orderByDesc(Goods::getCreatedAt);
        IPage<Goods> page = goodsService.page(new Page<>(params.getCurrent(), params.getPageSize()), wrapper);
        return ResData.success(page.getRecords(), page.getTotal());
    }

    @PostMapping("modify")
    public ResData<Void> modify(@Validated @RequestBody Goods goods) {
        Goods existing = goodsService.getOne(Wrappers.<Goods>lambdaQuery()
            .eq(Goods::getGoodsId, goods.getGoodsId())
            .last("limit 1"));
        if (existing != null && !Boolean.TRUE.equals(existing.getDeleted()) && !Objects.equals(existing.getId(), goods.getId())) {
            return ResData.fail("货品ID已存在");
        }
        if (existing != null && Boolean.TRUE.equals(existing.getDeleted()) && !StringUtils.hasText(goods.getId())) {
            goods.setId(existing.getId());
            goods.setDeleted(false);
        }
        return goodsService.saveOrUpdate(goods) ? ResData.success() : ResData.fail("保存失败");
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody Goods goods) {
        boolean success = goods.getId() != null && goodsService.update(Wrappers.<Goods>lambdaUpdate()
            .eq(Goods::getId, goods.getId())
            .set(Goods::getDeleted, true));
        return success ? ResData.success() : ResData.fail("删除失败");
    }

    @PostMapping("deleteBatch")
    public ResData<Void> deleteBatch(@RequestBody List<String> ids) {
        boolean success = !ids.isEmpty() && goodsService.update(Wrappers.<Goods>lambdaUpdate()
            .in(Goods::getId, ids)
            .set(Goods::getDeleted, true));
        return success ? ResData.success() : ResData.fail("删除失败");
    }

    @GetMapping("info")
    public ResData<Goods> info(@RequestParam String id) {
        return ResData.success(goodsService.getById(id));
    }

    @GetMapping("template")
    public void template(HttpServletResponse response) throws IOException {
        writeGoodsWorkbook(response, "货品资料导入模板.xlsx", List.of());
    }

    @PostMapping("export")
    public void export(@RequestBody GoodsSearchParams params, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<Goods> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Goods::getDeleted, false)
            .in(!CollectionUtils.isEmpty(params.getIds()), Goods::getId, params.getIds())
            .like(StringUtils.hasText(params.getGoodsId()), Goods::getGoodsId, params.getGoodsId())
            .like(StringUtils.hasText(params.getGenericName()), Goods::getGenericName, params.getGenericName())
            .orderByDesc(Goods::getCreatedAt);
        writeGoodsWorkbook(response, "货品资料.xlsx", goodsService.list(wrapper));
    }

    @PostMapping("import")
    public ResData<String> importExcel(@RequestParam("file") MultipartFile file) {
        int saved = 0;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (!BaseDataExcelUtils.headerMatches(sheet.getRow(0), GOODS_HEADERS)) {
                return ResData.fail("导入模板不正确，请先下载模板");
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (BaseDataExcelUtils.emptyRow(row, GOODS_HEADERS.length)) {
                    continue;
                }
                String goodsId = BaseDataExcelUtils.cellValue(row, 0);
                String genericName = BaseDataExcelUtils.cellValue(row, 1);
                String manufacturer = BaseDataExcelUtils.cellValue(row, 2);
                String specification = BaseDataExcelUtils.cellValue(row, 3);
                String unit = BaseDataExcelUtils.cellValue(row, 4);
                if (!StringUtils.hasText(goodsId) || !StringUtils.hasText(genericName)
                    || !StringUtils.hasText(manufacturer) || !StringUtils.hasText(specification)
                    || !StringUtils.hasText(unit)) {
                    return ResData.fail("第" + (i + 1) + "行存在必填项为空");
                }
                Goods goods = goodsService.getOne(Wrappers.<Goods>lambdaQuery()
                    .eq(Goods::getGoodsId, goodsId)
                    .last("limit 1"));
                if (goods == null) {
                    goods = new Goods();
                    goods.setGoodsId(goodsId);
                } else if (Boolean.TRUE.equals(goods.getDeleted())) {
                    goods.setDeleted(false);
                }
                goods.setGenericName(genericName);
                goods.setManufacturer(manufacturer);
                goods.setSpecification(specification);
                goods.setUnit(unit);
                goodsService.saveOrUpdate(goods);
                saved++;
            }
            return ResData.success("导入成功：" + saved + "条");
        } catch (IOException e) {
            return ResData.fail("文件读取失败，请确认文件格式为 xlsx 或 xls");
        }
    }

    private void writeGoodsWorkbook(HttpServletResponse response, String filename, List<Goods> goodsList) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", BaseDataExcelUtils.contentDisposition(filename));
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("货品资料");
            BaseDataExcelUtils.writeHeader(workbook, sheet, GOODS_HEADERS);
            for (int i = 0; i < goodsList.size(); i++) {
                Goods goods = goodsList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(goods.getGoodsId());
                row.createCell(1).setCellValue(goods.getGenericName());
                row.createCell(2).setCellValue(goods.getManufacturer());
                row.createCell(3).setCellValue(goods.getSpecification());
                row.createCell(4).setCellValue(goods.getUnit());
            }
            workbook.write(response.getOutputStream());
        }
    }
}
