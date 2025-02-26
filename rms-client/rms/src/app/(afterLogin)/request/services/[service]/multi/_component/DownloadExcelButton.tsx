import React, {useEffect, useState} from 'react';
import ExcelJS from 'exceljs';
import {Extension, ExtensionType} from "@/model/Extension";
import style from './downloadExcelButton.module.css'
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload} from "@fortawesome/free-solid-svg-icons";
import {usePathname} from "next/navigation";
import {getSampleType} from "@/app/(afterLogin)/request/services/_api/getSampleType";
import {getOrganization} from "@/app/(afterLogin)/request/services/[service]/multi/_api/getOrganization";
import {useSession} from "next-auth/react";
import {SampleType} from "@/model/SampleType";
import {Organization} from "@/model/Organization";

interface DownloadExcelButtonProps {
    extensions: Extension[];
}

export default function DownloadExcelButton({ extensions }: DownloadExcelButtonProps) {
    const { data: session } = useSession();
    const userId = session?.user?.id ?? '';
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [sampleTypeList, setSampleTypeList] = useState<SampleType[]>();
    const [institutionList, setInstitutionList] = useState<Organization[]>();

    const handleDownload = async () => {
        const headers = [
            "Institution Name", //List
            "Patient Name",
            "MRN",
            "Date of Birth (YYYY-MM-DD)", // Date
            "Gender", // List
            "Sample Type",
            "Collection Date (YYYY-MM-DD)", // Date
            "Quantity", // Decimal
            "Medical Department",
            "Ward",
            "Physician Name",
            ...extensions.map(extension => extension.name),
            "Memo",
        ];
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const formattedDate = `${year}_${month}_${day}`;

        const workbook = new ExcelJS.Workbook();
        const worksheet = workbook.addWorksheet('Sheet1');

        worksheet.addRow(headers);

        worksheet.columns = headers.map(header => {
            return { width: Math.max(header!.length, 20) };
        });

        const institutionSheet = workbook.addWorksheet('Institutions');
        const sampleTypeSheet = workbook.addWorksheet('SampleTypes');

        institutionList?.forEach((institution, index) => {
            institutionSheet.getCell(`A${index + 1}`).value = `${institution.id}/${institution.name}`;
        });

        sampleTypeList?.forEach((sampleType, index) => {
            sampleTypeSheet.getCell(`A${index + 1}`).value = `${sampleType.id}/${sampleType.name}`;
        });


        const startRow = 2;
        const endRow = 10000;

        for (let rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            if (institutionList && institutionList.length > 0) {
                worksheet.getCell(rowIndex, 1).dataValidation = {
                    type: 'list',
                    allowBlank: true,
                    formulae: [`Institutions!$A$1:$A$${institutionList.length}`],
                    showErrorMessage: true,
                    errorTitle: 'Invalid Institution',
                    error: 'Please select a valid institution from the list.',
                };
            }

            worksheet.getCell(rowIndex, 4).dataValidation = {
                type: 'date',
                allowBlank: true,
                showErrorMessage: true,
                errorTitle: 'Invalid Date',
                error: 'Please enter a valid date (YYYY/MM/DD).',
                formulae: [new Date(1900, 0, 1), new Date(2100, 11, 31)]
            };

            worksheet.getCell(rowIndex, 5).dataValidation = {
                type: 'list',
                allowBlank: true,
                formulae: ['"Male,Female"'],
                showErrorMessage: true,
                errorTitle: 'Invalid Gender',
                error: 'Please select "Male" or "Female".',
            };

            if (sampleTypeList && sampleTypeList.length > 0) {
                worksheet.getCell(rowIndex, 6).dataValidation = {
                    type: 'list',
                    allowBlank: true,
                    formulae: [`SampleTypes!$A$1:$A$${sampleTypeList.length}`],
                    showErrorMessage: true,
                    errorTitle: 'Invalid Sample Type',
                    error: 'Please select a valid sample type from the list.',
                };
            }

            worksheet.getCell(rowIndex, 7).dataValidation = {
                type: 'date',
                allowBlank: true,
                showErrorMessage: true,
                errorTitle: 'Invalid Date',
                error: 'Please enter a valid date (YYYY/MM/DD).',
                formulae: [new Date(1900, 0, 1), new Date(2100, 11, 31)]
            };

            worksheet.getCell(rowIndex, 8).dataValidation = {
                type: 'decimal',
                allowBlank: true,
                operator: 'between',
                formulae: [0, 200],
                showErrorMessage: true,
                errorTitle: 'Invalid Number',
                error: 'Please enter a valid decimal number.',
            };

            extensions.forEach((extension) => {
                const columnIndex = headers.indexOf(extension.name) + 1;
                const cell = worksheet.getCell(rowIndex, columnIndex);

                switch (extension.type) {
                    case ExtensionType.LIST:
                        cell.dataValidation = {
                            type: 'list',
                            allowBlank: !extension.required,
                            formulae: [`"${extension.regex!.replace(/\\b\(\?:|\)\\b/g, '').split('|').join(',')}"`],
                            showErrorMessage: true,
                            errorTitle: 'Invalid Selection',
                            error: `Please select a valid option for ${extension.name}.`,
                        };
                        break;
                    case ExtensionType.INTEGER:
                        cell.dataValidation = {
                            type: 'whole',
                            allowBlank: !extension.required,
                            operator: 'between',
                            formulae: [0, 1000],
                            showErrorMessage: true,
                            errorTitle: 'Invalid Number',
                            error: `Please enter a valid integer for ${extension.name}.`,
                        };
                        break;
                    case ExtensionType.NUMBER:
                        cell.dataValidation = {
                            type: 'decimal',
                            allowBlank: !extension.required,
                            operator: 'between',
                            formulae: [-1000, 1000],
                            showErrorMessage: true,
                            errorTitle: 'Invalid Decimal',
                            error: `Please enter a valid number for ${extension.name}.`,
                        };
                        break;
                    case ExtensionType.BOOLEAN:
                        cell.dataValidation = {
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
            });
        }

        const buffer = await workbook.xlsx.writeBuffer();
        const blob = new Blob([buffer], { type: "application/octet-stream" });

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${serviceId}_${formattedDate}.xlsx`;
        a.click();
        window.URL.revokeObjectURL(url);
    };

    useEffect(() => {
        const fetchData = async () => {
            const sampleTypeResponse = await getSampleType(serviceId);
            const sampleType = await sampleTypeResponse.json();
            setSampleTypeList(sampleType);

            const institutionResponse = await getOrganization(userId);
            const institution = await institutionResponse.json();
            setInstitutionList(institution);
        };
        fetchData();
    }, []);

    return (
        <button className={style.download} onClick={handleDownload}>
            Download Excel&nbsp;
            <FontAwesomeIcon className={style.downloadIcon} icon={faDownload} />
        </button>
    );
}
