'use client';

import style from "./extensionTable.module.css";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useEffect, useState} from "react";
import {Extensions} from "@/model/ServiceExtensionAndSampleType";
import {faFloppyDisk} from "@fortawesome/free-regular-svg-icons";
import {getExtensions} from "@/app/(afterLogin)/request/management/extension/_api/getExtensions";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";

export default function ExtensionTable() {
    const [extensionData, setExtensionData] = useState<Extensions[]>([]);

    const extensionOptions: SelectBoxOption[] = [
        {value: 'Boolean', name: 'Boolean'},
        {value: 'String', name: 'String'},
        {value: 'Number', name: 'Number'},
        {value: 'List', name: 'List'}
    ];

    const fetchData = async () => {
        try {
            const response = await getExtensions()
            const data = await response.json();
            setExtensionData(data as Extensions[]);
        } catch(error) {
            console.error("Failed to fetch data:", error);
            setExtensionData([]);
        }
    }

    const handleSaveExtensionClick = (id: string) => {
        alert(`${id} save`);
    }

    const extensionChange = () => {
        alert('siuuuuuu');
    }

    const mapRegexToType = (regex: string): string => {
        if (regex === "\\b(?:true|false)\\b") {
            return "Boolean";
        } else if (regex === "-?\\d+(\\.\\d+)?") {
            return "Number";
        } else if (regex === "-?\\d+") {
            return "Number";
        } else if (regex.includes("|")) {
            return "List";
        } else {
            return "String";
        }
    }

    const mapRegexToValue = (regex: string): string => {
        if (regex.includes("|")) {
            return regex
                .replace(/\\b|\b/g, '')
                .replace(/\\|\(|\)|\?:/g, '')
                .split('|')
                .filter(value => value !== 'true' && value !== 'false')
                .join(',');
        }
        return "";
    };

    useEffect(() => {
        fetchData()
    }, []);

    return (
        <>
            <section className={style.tableContainer}>
                <table className={style.table}>
                    <thead>
                    <tr>
                        <th className={style.headName}>Name</th>
                        <th className={style.head}>Type</th>
                        <th className={style.head}>List Value</th>
                        <th className={style.headSelect}>Select</th>
                        <th className={style.head}>Save</th>
                    </tr>
                    </thead>
                    <tbody>
                    {extensionData && extensionData.length > 0 && extensionData.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                            <td>{row.name}</td>
                            <td>{mapRegexToType(row.regex)}</td>
                            <td>{mapRegexToValue(row.regex)}</td>
                            <td>
                                <SelectBox
                                    label={''}
                                    options={extensionOptions}
                                    onChange={extensionChange}
                                    width={'5vw'}
                                />
                            </td>
                            <td>
                                <FontAwesomeIcon
                                    className={style.change}
                                    icon={faFloppyDisk}
                                    onClick={() => handleSaveExtensionClick(row.id)}
                                />
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </section>
        </>
    );
}