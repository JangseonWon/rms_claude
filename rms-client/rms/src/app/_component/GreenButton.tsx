"use client"

import style from "@/app/_component/greenButton.module.css"

type Props = {
    name: string
    onClick?: () => void
}
export default function GreenButton({name, onClick}: Props) {
    return (
        <button className={style.greenButton} onClick={onClick}>{name}</button>
    )
}