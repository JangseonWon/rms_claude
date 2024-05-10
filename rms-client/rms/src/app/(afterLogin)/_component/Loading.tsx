"use client"

import Image from "next/image";
import loadingImg from "@/../public/loading.gif"


export default function Loading() {
    return <Image src={loadingImg} alt={"loading"}/>
}