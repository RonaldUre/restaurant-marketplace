param(
  # Si el script está dentro de la carpeta objetivo, no toques esto.
  [string]$Root = $PSScriptRoot,
  # Escribe cambios (si no se pasa, solo hace preview)
  [switch]$Write,
  # Opcional: generar records para *Request / *Response
  [switch]$AsRecords
)

# Normaliza la ruta raíz
$Root = (Resolve-Path -Path $Root).Path

function Get-PackageName {
  param($FilePath, $Root)
  $dir = Split-Path -Path $FilePath -Parent
  # Parte relativa desde la raíz
  $rel = $dir.Substring($Root.Length).TrimStart('\','/')
  # Reemplaza separadores por puntos
  $suffix = ($rel -replace '[\\/]+','.')
  $pkgBase = "com.ronaldure.restaurantmarketplace.restaurant_marketplace"
  if ($suffix) { "$pkgBase.$suffix" } else { $pkgBase }
}

function Guess-Type {
  param($File)
  $name = [System.IO.Path]::GetFileNameWithoutExtension($File)
  $dir  = Split-Path -Path $File -Parent

  if ($name -match 'Exception$') { return 'exception' }
  if ($name -match '(Status|Type|State|Mode|Category|Role|Enum)$') { return 'enum' }

  # Interfaces por patrón de carpeta o sufijo
  if ($dir -match "\\ports\\in|\\ports\\out" -or $name -match '(Port|Repository|UseCase|Projection|Query|View)$') {
    return 'interface'
  }

  # Clases típicas
  if ($name -match '(Controller|Service|Adapter|Entity|Mapper|Factory|Handler|Command)$') { return 'class' }

  # DTOs públicos (opcionalmente como records)
  if ($name -match '(Request|Response)$') { return 'dto' }

  return 'class'
}

function Make-Content {
  param($Pkg, $Name, $Kind, $AsRecords)

  switch ($Kind) {
    'exception' {
@"
package $Pkg;

public class $Name extends RuntimeException {
    public $Name() { super(); }
    public $Name(String message) { super(message); }
    public $Name(String message, Throwable cause) { super(message, cause); }
}
"@
    }
    'interface' {
@"
package $Pkg;

public interface $Name {
}
"@
    }
    'enum' {
@"
package $Pkg;

public enum $Name {
    // TODO: define values
}
"@
    }
    'dto' {
      if ($AsRecords) {
@"
package $Pkg;

public record $Name() {
    // TODO: add fields
}
"@
      } else {
@"
package $Pkg;

public class $Name {
    // TODO: add fields, constructors, getters
}
"@
      }
    }
    default {
@"
package $Pkg;

public class $Name {
    // TODO: implement
}
"@
    }
  }
}

# Busca .java vacíos (solo espacios en blanco cuentan como vacíos)
$files = Get-ChildItem -Path $Root -Recurse -Filter *.java -ErrorAction Stop
$targets = @()

foreach ($f in $files) {
  $raw = (Get-Content -Raw -ErrorAction SilentlyContinue -Path $f.FullName)
  if ($null -eq $raw -or $raw.Trim().Length -eq 0) {
    $targets += $f
  }
}

if ($targets.Count -eq 0) {
  Write-Host "No hay archivos .java vacíos para inicializar en: $Root"
  exit 0
}

Write-Host "Inicializando $($targets.Count) archivos..."

foreach ($f in $targets) {
  $pkg  = Get-PackageName -FilePath $f.FullName -Root $Root
  $name = [System.IO.Path]::GetFileNameWithoutExtension($f.Name)
  $kind = Guess-Type -File $f.FullName
  $content = Make-Content -Pkg $pkg -Name $name -Kind $kind -AsRecords:$AsRecords

  if ($Write) {
    Set-Content -Path $f.FullName -Value $content -Encoding UTF8
  } else {
    Write-Host "---- PREVIEW: $($f.FullName)  [$kind]"
    $content | Write-Host
  }
}

if ($Write) {
  Write-Host "Listo. Clases/interfaces inicializadas."
} else {
  Write-Host "`nEjecuta de nuevo con -Write para aplicar cambios reales."
}

