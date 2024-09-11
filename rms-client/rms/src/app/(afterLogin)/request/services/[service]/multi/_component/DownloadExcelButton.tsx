import React from 'react';
import ExcelJS from 'exceljs';
import {Extensions} from "@/model/ServiceExtensionAndSampleType";
import style from './downloadExcelButton.module.css'
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {usePathname} from "next/navigation";

interface DownloadExcelButtonProps {
    extensions: Extensions[];
}

export default function DownloadExcelButton({ extensions }: DownloadExcelButtonProps) {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);

    const handleDownload = async () => {
        const headers = [
            "Registration Date (YYYY/MM/DD)", // Date
            "Ward",
            "Patient Name",
            "Personal ID Number (YYYY/MM/DD)", // Date
            "Gender (Male, Female)", // List
            "Physician",
            "Collection Date (YYYY/MM/DD)", // Date
            "Chart Number",
            "Code",
            "Gestational Age", // Integer
            "Weight", // Decimal
            "Fetuses (1 or 2)", // Integer
            "Quantity", // Decimal
            "Notes",
            "Race (Genome Health Premium)",
            ...extensions.map(extension => extension.name),
        ];
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const formattedDate = `${year}_${month}_${day}`;

        const workbook = new ExcelJS.Workbook();
        const worksheet = workbook.addWorksheet('Sheet1');

        worksheet.addRow(headers);


        for (let i = 2; i <= 101; i++) {
            worksheet.getCell(i, 1).dataValidation = worksheet.getCell(i, 4).dataValidation = worksheet.getCell(i, 7).dataValidation = {
                type: 'date',
                allowBlank: true,
                showErrorMessage: true,
                errorTitle: 'Invalid Date',
                error: 'Please enter a valid date (YYYY/MM/DD).',
                formulae: [new Date(1900, 0, 1), new Date(2100, 11, 31)]
            };

            worksheet.getCell(i, 5).dataValidation = {
                type: 'list',
                allowBlank: true,
                formulae: ['"Male,Female"'],
                showErrorMessage: true,
                errorTitle: 'Invalid Gender',
                error: 'Please select "Male" or "Female".',
            };

            worksheet.getCell(i, 10).dataValidation = worksheet.getCell(i, 12).dataValidation = {
                type: 'whole',
                allowBlank: true,
                operator: 'between',
                formulae: [1, 40],
                showErrorMessage: true,
                errorTitle: 'Invalid Number',
                error: 'Please enter a valid integer.',
            };

            worksheet.getCell(i, 11).dataValidation = worksheet.getCell(i, 13).dataValidation = {
                type: 'decimal',
                allowBlank: true,
                operator: 'between',
                formulae: [0, 200],
                showErrorMessage: true,
                errorTitle: 'Invalid Number',
                error: 'Please enter a valid decimal number.',
            };
        }

        extensions.forEach((extension, index) => {
            const columnIndex = headers.indexOf(extension.name) + 1;
            for (let i = 2; i <= 101; i++) {
                switch (extension.type) {
                    case 'List':
                        worksheet.getCell(i, columnIndex).dataValidation = {
                            type: 'list',
                            allowBlank: !extension.required,
                            formulae: [`"${extension.regex.replace(/\\b\(\?:|\)\\b/g, '').split('|').join(',')}"`], // regex를 리스트로 변환
                            showErrorMessage: true,
                            errorTitle: 'Invalid Selection',
                            error: `Please select a valid option for ${extension.name}.`,
                        };
                        break;
                    case 'Int':
                        worksheet.getCell(i, columnIndex).dataValidation = {
                            type: 'whole',
                            allowBlank: !extension.required,
                            operator: 'between',
                            formulae: [0, 1000],
                            showErrorMessage: true,
                            errorTitle: 'Invalid Number',
                            error: `Please enter a valid integer for ${extension.name}.`,
                        };
                        break;
                    case 'Number':
                        worksheet.getCell(i, columnIndex).dataValidation = {
                            type: 'decimal',
                            allowBlank: !extension.required,
                            operator: 'between',
                            formulae: [-1000, 1000],
                            showErrorMessage: true,
                            errorTitle: 'Invalid Decimal',
                            error: `Please enter a valid number for ${extension.name}.`,
                        };
                        break;
                    case 'Boolean':
                        worksheet.getCell(i, columnIndex).dataValidation = {
                            type: 'list',
                            allowBlank: !extension.required,
                            formulae: ['"true,false"'],
                            showErrorMessage: true,
                            errorTitle: 'Invalid Boolean',
                            error: `Please select true or false for ${extension.name}.`,
                        };
                        break;
                    default:
                        break;
                }
            }
        });

        const buffer = await workbook.xlsx.writeBuffer();
        const blob = new Blob([buffer], { type: "application/octet-stream" });

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${serviceId}_${formattedDate}.xlsx`;
        a.click();
        window.URL.revokeObjectURL(url);
    };

    return (
        <button className={style.download} onClick={handleDownload}>
            Download Excel&nbsp;
            <FontAwesomeIcon className={style.downloadIcon} icon={faDownload} />
        </button>
    );
}
