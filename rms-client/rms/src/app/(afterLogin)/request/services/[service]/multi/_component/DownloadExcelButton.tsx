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
            "Sample Type", //List
            "Institution", //List
            "Registration Date", // Date
            "Ward",
            "Patient Name",
            "Personal ID Number", // Date
            "Gender", // List
            "Physician",
            "Medical Department",
            "Collection Date", // Date
            "Chart Number",
            "Gestational Age", // Integer
            "Weight", // Decimal
            "Fetuses", // Integer
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

        worksheet.columns = headers.map(header => {
            return { width: Math.max(header!.length, 20) };
        });

        const sampleTypeFormulae = sampleTypeList?.map(sample => `${sample.id}/${sample.name}`).join(',') || '';
        const institutionFormulae = institutionList?.map(institution => `${institution.id}/${institution.name}`).join(',') || '';

        for (let i = 2; i <= 101; i++) {
            worksheet.getCell(i, 1).dataValidation = {
                type: 'list',
                allowBlank: true,
                formulae: [`"${sampleTypeFormulae}"`],
                showErrorMessage: true,
                errorTitle: 'Invalid Gender',
                error: 'Please check List.',
            };

            worksheet.getCell(i, 2).dataValidation = {
                type: 'list',
                allowBlank: true,
                formulae: [`"${institutionFormulae}"`],
                showErrorMessage: true,
                errorTitle: 'Invalid Gender',
                error: 'Please check List.',
            };

            worksheet.getCell(i, 3).dataValidation = worksheet.getCell(i, 6).dataValidation = worksheet.getCell(i, 9).dataValidation = {
                type: 'date',
                allowBlank: true,
                showErrorMessage: true,
                errorTitle: 'Invalid Date',
                error: 'Please enter a valid date (YYYY/MM/DD).',
                formulae: [new Date(1900, 0, 1), new Date(2100, 11, 31)]
            };

            worksheet.getCell(i, 7).dataValidation = {
                type: 'list',
                allowBlank: true,
                formulae: ['"Male,Female"'],
                showErrorMessage: true,
                errorTitle: 'Invalid Gender',
                error: 'Please select "Male" or "Female".',
            };

            worksheet.getCell(i, 12).dataValidation = worksheet.getCell(i, 14).dataValidation = {
                type: 'whole',
                allowBlank: true,
                operator: 'between',
                formulae: [1, 40],
                showErrorMessage: true,
                errorTitle: 'Invalid Number',
                error: 'Please enter a valid integer.',
            };

            worksheet.getCell(i, 13).dataValidation = worksheet.getCell(i, 13).dataValidation = {
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
                    case ExtensionType.LIST:
                        worksheet.getCell(i, columnIndex).dataValidation = {
                            type: 'list',
                            allowBlank: !extension.required,
                            formulae: [`"${extension.regex!.replace(/\\b\(\?:|\)\\b/g, '').split('|').join(',')}"`], // regex를 리스트로 변환
                            showErrorMessage: true,
                            errorTitle: 'Invalid Selection',
                            error: `Please select a valid option for ${extension.name}.`,
                        };
                        break;
                    case ExtensionType.INTEGER:
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
                    case ExtensionType.NUMBER:
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
                    case ExtensionType.BOOLEAN:
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
